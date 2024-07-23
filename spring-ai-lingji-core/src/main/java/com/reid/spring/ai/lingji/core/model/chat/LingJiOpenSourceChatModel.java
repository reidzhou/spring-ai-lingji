package com.reid.spring.ai.lingji.core.model.chat;

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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LingJiOpenSourceChatModel implements ChatModel, StreamingChatModel {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String apiKey;

    private final RestClient restClient;

    private final WebClient webClient;

    private final LingJiOpenSourceChatOptions options;

    public LingJiOpenSourceChatModel(String apiKey) {
        this(apiKey, RestClient.builder().build(), WebClient.builder().build());
    }

    public LingJiOpenSourceChatModel(String apiKey, RestClient restClient, WebClient webClient) {
        this(apiKey, restClient, webClient, Constants.DEFAULT_OPTIONS);
    }

    public LingJiOpenSourceChatModel(String apiKey,
                                     RestClient restClient,
                                     WebClient webClient,
                                     LingJiOpenSourceChatOptions options) {
        Assert.notNull(apiKey, "The apiKey can not be null.");
        Assert.notNull(restClient, "The restClient can not be null.");
        Assert.notNull(webClient, "The webClient can not be null.");
        Assert.notNull(options, "The options can not be null.");

        this.apiKey = apiKey;
        this.restClient = restClient;
        this.webClient = webClient;
        this.options = options;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        Assert.notNull(prompt, "The prompt can not be null.");
        LingJiOpenSourceChatRequest request = this.createRequest(prompt);
        return createChatResponse(request, this.doRPC(request));
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        Assert.notNull(prompt, "The request body can not be null.");
        LingJiOpenSourceChatRequest request = this.createRequest(prompt);
        return this.doStreamRPC(request);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return this.options;
    }

    private LingJiOpenSourceChatRequest createRequest(Prompt prompt) {
        LingJiOpenSourceChatRequest request = new LingJiOpenSourceChatRequest();

        ModelOptions requestOptions = prompt.getOptions();

        LingJiOpenSourceChatOptions combinedOptions = ModelOptionsUtils.merge(
                requestOptions, this.options, LingJiOpenSourceChatOptions.class);

        request.setModel(combinedOptions.getModel());

        List<LingJiOpenSourceChatRequest.Message> messages = prompt.getInstructions()
                .stream()
                .map(message -> {

                    MessageType messageType = message.getMessageType();

                    // do not support function call here
                    if (messageType.equals(MessageType.FUNCTION)) {
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

                    return new LingJiOpenSourceChatRequest.Message(role, message.getContent());
                })
                .filter(Objects::nonNull)
                .toList();

        request.setInput(new LingJiOpenSourceChatRequest.Input(messages));

        LingJiOpenSourceChatRequest.Parameters parameters = new LingJiOpenSourceChatRequest.Parameters(combinedOptions);
        request.setParameters(parameters);

        return request;
    }

    private LingJiOpenSourceChatResponse doRPC(LingJiOpenSourceChatRequest request) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(
                    "Request LingJi OpenSource Chat Model [ url = {}, request = {} ]",
                    Constants.LING_JI_CHAT_URL, ModelOptionsUtils.toJsonString(request));
        }

        ResponseEntity<LingJiOpenSourceChatResponse> responseEntity = this.restClient
                .post()
                .uri(Constants.LING_JI_CHAT_URL)
                .headers((headers) -> {
                    headers.setBearerAuth(this.apiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(request)
                .retrieve()
                .toEntity(LingJiOpenSourceChatResponse.class);

        LingJiOpenSourceChatResponse lingJiOpenSourceChatResponse = responseEntity.getBody();
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();

        if (httpStatusCode.is2xxSuccessful()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(
                        "Request LingJi OpenSource Chat Model Success [ response = {} ]",
                        ModelOptionsUtils.toJsonString(lingJiOpenSourceChatResponse));
            }

            return lingJiOpenSourceChatResponse;
        } else {

            if (lingJiOpenSourceChatResponse == null) {
                this.logger.debug(
                        "Request LingJi OpenSource Chat Model failed [ http_code = {} ]",
                        httpStatusCode.value());

                throw new LingJiRPCException(httpStatusCode.value());
            } else {

                this.logger.debug(
                        "Request LingJi OpenSource Chat Model failed [ http_code = {}, err = {} ]",
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

    private ChatResponse createChatResponse(LingJiOpenSourceChatRequest request, LingJiOpenSourceChatResponse response) {
        String resultFormat = request.getParameters().resultFormat();

        ChatGenerationMetadata chatGenerationMetadata = null;
        String content = null;
        if (Constants.LING_JI_RESULT_FORMAT.equals(resultFormat)) {
            content = response.getOutput().text();
            chatGenerationMetadata = ChatGenerationMetadata.from(response.getOutput().finishReason(), null);
        } else {
            LingJiOpenSourceChatResponse.Choice choice = response.getOutput().choices().get(0);
            content = choice.message().content();
            chatGenerationMetadata = ChatGenerationMetadata.from(choice.finishReason(), null);
        }

        Generation generation = new Generation(content).withGenerationMetadata(chatGenerationMetadata);

        LingJiOpenSourceChatResponseMetadata chatResponseMetadata = new LingJiOpenSourceChatResponseMetadata(
                new LingJiOpenSourceChatResponseMetadata.LingJiOpenSourceChatUsage(response.getUsage())
        );

        return new ChatResponse(Collections.singletonList(generation), chatResponseMetadata);
    }

    private Flux<ChatResponse> doStreamRPC(LingJiOpenSourceChatRequest request) {
        return this.webClient
                .post()
                .uri(Constants.LING_JI_CHAT_URL)
                .headers((headers) -> {
                    headers.setBearerAuth(this.apiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add(Constants.HEADER_SSE, "enable");
                })
                .body(Mono.just(request), LingJiOpenSourceChatRequest.class)
                .retrieve()
                .bodyToFlux(String.class)
                .map(s -> ModelOptionsUtils.jsonToObject(s, LingJiOpenSourceChatResponse.class))
                .map(response -> this.createChatResponse(request, response));
    }

    private LingJiOpenSourceChatResponse merge(LingJiOpenSourceChatRequest.Parameters parameters,
                                               LingJiOpenSourceChatResponse previous,
                                               LingJiOpenSourceChatResponse current) {
        String format = parameters.resultFormat();

        if (parameters.incrementalOutput()) {

            String content = null;

            List<LingJiOpenSourceChatResponse.Choice> choices = null;
            if (format.equals(Constants.LING_JI_RESULT_FORMAT)) {
                content = previous.getOutput().text() + current.getOutput().text();
            } else {
                LingJiOpenSourceChatResponse.Choice currentChoice = current.getOutput().choices().get(0);

                content = previous.getOutput().choices().get(0).message().content() +
                        currentChoice.message().content();

                choices = new ArrayList<>();
                LingJiOpenSourceChatResponse.Choice choice = new LingJiOpenSourceChatResponse.Choice(
                        currentChoice.finishReason(),
                        new LingJiOpenSourceChatResponse.Message(currentChoice.message().role(), content)
                );
                choices.add(choice);
            }

            LingJiOpenSourceChatResponse mergedResponse = new LingJiOpenSourceChatResponse();
            mergedResponse.setCode(current.getCode());
            mergedResponse.setMessage(current.getMessage());
            mergedResponse.setRequestId(current.getRequestId());
            mergedResponse.setUsage(current.getUsage());
            mergedResponse.setOutput(
                    new LingJiOpenSourceChatResponse.Output(
                            content,
                            current.getOutput().finishReason(),
                            choices
                    )
            );

            return mergedResponse;
        } else {
            return current;
        }
    }

    private boolean isStreamEnd(LingJiOpenSourceChatRequest.Parameters parameters, LingJiOpenSourceChatResponse response) {
        String format = parameters.resultFormat();
        if (format.equals(Constants.LING_JI_RESULT_FORMAT)) {
            return response.getOutput().finishReason() != null;
        } else {
            return response.getOutput().choices().get(0).finishReason() != null;
        }
    }

}
