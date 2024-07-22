package com.reid.spring.ai.lingji.core.model.chat;

import com.reid.spring.ai.lingji.core.exception.LingJiRPCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.MessageType;
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
        return createChatResponse(this.doRPC(this.createRequest(prompt)));
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return null;
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

    private ChatResponse createChatResponse(LingJiOpenSourceChatResponse response) {
        LingJiOpenSourceChatResponseMetadata metadata = new LingJiOpenSourceChatResponseMetadata(
                new LingJiOpenSourceChatResponseMetadata.LingJiOpenSourceChatUsage(response.getUsage())
        );

        List<Generation> generations = response.getOutput()
                .choices()
                .stream()
                .map(choice -> new Generation(choice.message().content()))
                .toList();

        return new ChatResponse(generations, metadata);
    }

}
