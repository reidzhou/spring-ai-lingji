package com.reid.spring.ai.lingji.core.model.chat;

import com.reid.spring.ai.lingji.core.exception.LingJiException;
import com.reid.spring.ai.lingji.core.exception.LingJiRPCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptions;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.AbstractFunctionCallSupport;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LingJiChatModel
        extends AbstractFunctionCallSupport<Message, LingJiChatRequest, LingJiChatResponse>
        implements ChatModel, StreamingChatModel {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String apiKey;

    private final RestClient restClient;

    private final WebClient webClient;

    private final LingJiChatOptions options;

    public LingJiChatModel(String apiKey) {
        this(apiKey, RestClient.builder().build(), WebClient.builder().build());
    }

    public LingJiChatModel(String apiKey, RestClient restClient, WebClient webClient) {
        this(apiKey, restClient, webClient, Constants.DEFAULT_OPTIONS);
    }

    public LingJiChatModel(String apiKey, RestClient restClient, WebClient webClient, LingJiChatOptions options) {
        this(apiKey, restClient, webClient, options, null);
    }

    public LingJiChatModel(String apiKey,
                           RestClient restClient,
                           WebClient webClient,
                           LingJiChatOptions options,
                           FunctionCallbackContext functionCallbackContext) {

        super(functionCallbackContext);

        Assert.notNull(apiKey, "The apiKey can not be null.");
        Assert.notNull(restClient, "The restClient can not be null.");
        Assert.notNull(webClient, "The webClient can not be null.");
        Assert.notNull(options, "The options can not be null.");

        this.apiKey = apiKey;
        this.restClient = restClient;
        this.webClient = webClient;
        this.options = options;

        this.handleFunctionCallbackConfigurations(this.options, false);
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        LingJiChatRequest request = this.doCreateRequest(prompt);
        try {
            return this.doCreateChatResponse(request, this.callWithFunctionSupport(request));
        } catch (Exception e) {
            throw new LingJiException("LingJi chat model exception", e);
        }
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        LingJiChatRequest request = this.doCreateRequest(prompt);
        return this.callWithFunctionSupportStream(request)
                .map(lingJiChatResponse -> this.doCreateChatResponse(request, lingJiChatResponse));
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return this.options;
    }

    @Override
    protected LingJiChatRequest doCreateToolResponseRequest(LingJiChatRequest previousRequest,
                                                            Message responseMessage,
                                                            List<Message> conversationHistory) {
        // toolCalls, one or more ?
        Message.ToolCall.Function function = responseMessage.toolCalls().get(0).function();
        String functionName = function.name();
        String arguments = function.arguments();

        this.logger.debug("Prepare to invoke function calling[ name = {}, arguments = {} ]", functionName, arguments);

        List<FunctionCallback> functionCallbacks = this.resolveFunctionCallbacks(Collections.singleton(functionName));
        if (functionCallbacks.isEmpty()) {
            throw new LingJiException(String.format("Function calling[ name = %s ] is not existed.", functionName));
        }

        // specific case
        if (arguments.equals("\"{}\"")) {
            arguments = "{}";
        }
        FunctionCallback functionCallback = functionCallbacks.get(0);
        String functionCallingResult = functionCallback.call(arguments);

        this.logger.debug(
                "Function calling invoked[ name = {}, arguments = {}, result = {} ]",
                functionName,
                arguments,
                functionCallingResult
        );

        Message functionCallingMessage = new Message(Role.tool, functionCallingResult, functionName);

        conversationHistory.add(functionCallingMessage);

        LingJiChatRequest currentRequest = new LingJiChatRequest();
        currentRequest.setModel(previousRequest.getModel());
        currentRequest.setParameters(previousRequest.getParameters());
        currentRequest.setInput(new Input(conversationHistory));

        this.logger.debug(
                "Create a new chat request with function calling result [ detail = {} ]",
                ModelOptionsUtils.toJsonString(currentRequest)
        );

        return currentRequest;
    }

    @Override
    protected List<Message> doGetUserMessages(LingJiChatRequest request) {
        return request.getInput().messages();
    }

    @Override
    protected Message doGetToolResponseMessage(LingJiChatResponse response) {
        Message toolResponseMessage = this.findFirstAssistantFunctionMessage(response);

        this.logger.debug(
                "Get tool response message from chat response[ response = {}, message = {} ]",
                ModelOptionsUtils.toJsonString(response),
                ModelOptionsUtils.toJsonString(toolResponseMessage)
        );

        return toolResponseMessage;
    }

    @Override
    protected LingJiChatResponse doChatCompletion(LingJiChatRequest request) {
        return this.doRPC(request);
    }

    @Override
    protected Flux<LingJiChatResponse> doChatCompletionStream(LingJiChatRequest request) {
        return this.doStreamRPC(request);
    }

    @Override
    protected boolean isToolFunctionCall(LingJiChatResponse response) {
        return this.findFirstAssistantFunctionMessage(response) != null;
    }

    private Message findFirstAssistantFunctionMessage(LingJiChatResponse response) {
        if (response == null) {
            return null;
        }

        Output output = response.getOutput();
        if (output == null) {
            return null;
        }

        List<Output.Choice> choices = output.choices();
        if (choices == null || choices.isEmpty()) {
            return null;
        }

        for (Output.Choice choice : choices) {
            Message message = choice.message();

            if (!Role.assistant.equals(message.role())) {
                continue;
            }

            List<Message.ToolCall> toolCalls = message.toolCalls();
            if (toolCalls == null) {
                continue;
            }

            for (Message.ToolCall toolCall : toolCalls) {

                if (!Constants.TOOL_TYPE_FUNCTION.equals(toolCall.type())) {
                    continue;
                }

                Message.ToolCall.Function function = toolCall.function();
                if (StringUtils.hasText(function.name())) {
                    return message;
                }

            }

        }

        return null;
    }

    private LingJiChatRequest doCreateRequest(Prompt prompt) {
        LingJiChatRequest request = new LingJiChatRequest();

        ModelOptions requestOptions = prompt.getOptions();
        if (requestOptions != null) {
            if (!(requestOptions instanceof LingJiChatOptions)) {
                throw new LingJiException("Prompt options should be LingJiChatOptions instance");
            }

            this.handleFunctionCallbackConfigurations((LingJiChatOptions) requestOptions, true);
        }

        LingJiChatOptions combinedOptions = ModelOptionsUtils.merge(
                requestOptions, this.options, LingJiChatOptions.class);

        request.setModel(combinedOptions.getModel());

        List<Message> messages = prompt.getInstructions()
                .stream()
                .map(message -> {

                    MessageType messageType = message.getMessageType();
                    if (messageType.equals(MessageType.FUNCTION)) {
                        logger.warn("Chat Prompt should be no function message, skip!");
                        return null;
                    }

                    Role role = null;
                    if (messageType.equals(MessageType.SYSTEM)) {
                        role = Role.system;
                    } else if (messageType.equals(MessageType.USER)) {
                        role = Role.user;
                    } else if (messageType.equals(MessageType.ASSISTANT)) {
                        role = Role.assistant;
                    }

                    if (role == null) {
                        logger.warn("Unknown prompt message type [" + messageType.getValue() + "], skip!");
                        return null;
                    }

                    return new Message(role, message.getContent());
                })
                .filter(Objects::nonNull)
                .toList();

        request.setInput(new Input(messages));

        Parameters parameters = new Parameters(combinedOptions);
        request.setParameters(parameters);

        this.doCheckRequest(request);

        this.logger.debug(
                "Convert prompt to lingji chat request[ source = {}, target = {} ]",
                ModelOptionsUtils.toJsonString(prompt),
                ModelOptionsUtils.toJsonString(request)
        );

        return request;
    }

    private void doCheckRequest(LingJiChatRequest request) {
        String model = request.getModel();
        com.reid.spring.ai.lingji.core.model.chat.ChatModel chatModel =
                com.reid.spring.ai.lingji.core.model.chat.ChatModel.map(model);

        Parameters parameters = request.getParameters();

        Float topP = parameters.topP();
        if (topP != null && (topP <= 0 || topP >= 1)) {
            throw new LingJiException("Range of top_p should be (0.0, 1.0)");
        }

        Float presencePenalty = parameters.presencePenalty();
        if (presencePenalty != null && (presencePenalty < -2 || presencePenalty > 2)) {
            throw new LingJiException("Range of presence_penalty should be [-2.0, 2.0]");
        }

        Float temperature = parameters.temperature();
        if (temperature != null && (temperature < 0 || temperature >= 2)) {
            throw new LingJiException("Range of temperature should be [0, 2)");
        }

        // todo: stop check
        // Object stop = parameters.stop();

        List<Parameters.Tool> tools = parameters.tools();
        Object toolChoice = parameters.toolChoice();
        if (tools == null || tools.isEmpty()) {
            if (toolChoice != null && !toolChoice.equals("none")) {
                throw new LingJiException("tool_choice should be null or 'none' when tools is empty");
            }
        } else {
            if (!chatModel.getSupportFunctionCalling()) {
                throw new LingJiException(
                        String.format("Model [%s] do not support function calling", chatModel.getName())
                );
            }

            if (!(chatModel.equals(com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_MAX)
                    || chatModel.equals(com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_MAX_0428)
                    || chatModel.equals(com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_MAX_0403)
                    || chatModel.equals(com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_TURBO))) {
                throw new LingJiException(
                        String.format(
                                "tool_choice only support models [%s, %s, %s, %s]",
                                com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_MAX.getName(),
                                com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_MAX_0428.getName(),
                                com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_MAX_0403.getName(),
                                com.reid.spring.ai.lingji.core.model.chat.ChatModel.QWEN_TURBO.getName()
                        )
                );
            }

            if (toolChoice != null) {
                if (!toolChoice.equals("auto") && !(toolChoice instanceof ToolChoice)) {
                    throw new LingJiException("tool_choice should be 'auto' or ToolChoice instance when tools is on");
                }
            }

        }


    }

    private ChatResponse doCreateChatResponse(LingJiChatRequest request, LingJiChatResponse response) {
        String resultFormat = request.getParameters().resultFormat();

        ChatGenerationMetadata chatGenerationMetadata = null;
        String content = null;
        if (Constants.LING_JI_RESULT_FORMAT.equals(resultFormat)) {
            content = response.getOutput().text();
            chatGenerationMetadata = ChatGenerationMetadata.from(response.getOutput().finishReason(), null);
        } else {
            Output.Choice choice = response.getOutput().choices().get(0);
            content = choice.message().content();
            chatGenerationMetadata = ChatGenerationMetadata.from(choice.finishReason(), null);
        }

        Generation generation = new Generation(content).withGenerationMetadata(chatGenerationMetadata);

        LingJiChatResponseMetadata chatResponseMetadata = new LingJiChatResponseMetadata(
                new LingJiChatUsage(response.getUsage())
        );

        ChatResponse chatResponse = new ChatResponse(Collections.singletonList(generation), chatResponseMetadata);

        this.logger.debug(
                "Convert lingji chat response to chat response [ source = {}, target = {} ]",
                ModelOptionsUtils.toJsonString(response),
                ModelOptionsUtils.toJsonString(chatResponse)
        );

        return chatResponse;
    }

    private LingJiChatResponse doRPC(LingJiChatRequest request) {
        this.logger.debug(
                "Request LingJi Chat Model [ url = {}, request = {} ]",
                Constants.LING_JI_CHAT_URL, ModelOptionsUtils.toJsonString(request));

        ResponseEntity<LingJiChatResponse> responseEntity = this.restClient
                .post()
                .uri(Constants.LING_JI_CHAT_URL)
                .headers((headers) -> {
                    headers.setBearerAuth(this.apiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(request)
                .retrieve()
                .toEntity(LingJiChatResponse.class);

        LingJiChatResponse lingJiOpenSourceChatResponse = responseEntity.getBody();
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();

        if (httpStatusCode.is2xxSuccessful()) {
            this.logger.debug(
                    "Request LingJi Chat Model Success [ response = {} ]",
                    ModelOptionsUtils.toJsonString(lingJiOpenSourceChatResponse));

            return lingJiOpenSourceChatResponse;
        } else {

            if (lingJiOpenSourceChatResponse == null) {
                this.logger.debug(
                        "Request LingJi Chat Model failed [ http_code = {} ]",
                        httpStatusCode.value());

                throw new LingJiRPCException(httpStatusCode.value());
            } else {

                this.logger.debug(
                        "Request LingJi Chat Model failed [ http_code = {}, err = {} ]",
                        httpStatusCode.value(), ModelOptionsUtils.toJsonString(lingJiOpenSourceChatResponse));

                throw new LingJiRPCException(
                        httpStatusCode.value(),
                        lingJiOpenSourceChatResponse.getRequestId(),
                        lingJiOpenSourceChatResponse.getCode(),
                        lingJiOpenSourceChatResponse.getMessage()
                );
            }
        }
    }

    private Flux<LingJiChatResponse> doStreamRPC(LingJiChatRequest request) {
        return this.webClient
                .post()
                .uri(Constants.LING_JI_CHAT_URL)
                .headers((headers) -> {
                    headers.setBearerAuth(this.apiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add(Constants.HEADER_SSE, "enable");
                })
                .body(Mono.just(request), LingJiChatRequest.class)
                .retrieve()
                .bodyToFlux(String.class)
                .map(s -> ModelOptionsUtils.jsonToObject(s, LingJiChatResponse.class));
    }


}
