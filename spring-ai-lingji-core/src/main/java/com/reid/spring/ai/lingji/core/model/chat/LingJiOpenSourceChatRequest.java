package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/**
 * see more https://help.aliyun.com/zh/dashscope/developer-reference/tongyi-qianwen-7b-14b-72b-api-detailes?spm=a2c4g.11186623.0.0.3d781b3fq82Rao#25745d61fbx49
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiOpenSourceChatRequest {

    @JsonProperty(value = "model")
    private String model;

    @JsonProperty(value = "input")
    private Input input;

    @JsonProperty(value = "parameters")
    private Parameters parameters;

    public record Input(
            @Deprecated
            @JsonProperty(value = "prompt")
            String prompt,
            @Deprecated
            @JsonProperty(value = "history")
            List<History> history,
            @JsonProperty(value = "messages")
            List<Message> messages
    ) {

        public Input(String prompt, List<History> history) {
            this(prompt, history, null);
        }

        public Input(List<Message> messages) {
            this(null, null, messages);
        }

    }

    /**
     * use {@link Message} instead of History
     * @param user
     * @param bot
     */
    @Deprecated
    public record History(
            @JsonProperty(value = "user")
            String user,
            @JsonProperty(value = "bot")
            String bot
    ) {}

    public record Message(
            @JsonProperty(value = "role")
            Role role,
            @JsonProperty(value = "content")
            String content
    ) {}

    public record Parameters(
            // "text" or "message"
            @JsonProperty(value = "result_format")
            String resultFormat,
            @JsonProperty(value = "seed")
            Integer seed,
            @JsonProperty(value = "max_tokens")
            Integer maxTokens,
            @JsonProperty(value = "top_p")
            Float topP,
            @JsonProperty(value = "top_k")
            Integer topK,
            @JsonProperty(value = "repetition_penalty")
            Float repetitionPenalty,
            @JsonProperty(value = "presence_penalty")
            Float presencePenalty,
            @JsonProperty(value = "temperature")
            Float temperature,

            // todo improve
            // multi modal type:
            // 1. String,               eg: "stop"
            // 2. String List,          eg: ["stop"]
            // 3. Integer List,         eg: [123, 123]
            // 4. Nested Integer List,  eg: [[123, 123]]
            @JsonProperty(value = "stop")
            Object stop,
            @JsonProperty(value = "incremental_output")
            Boolean incrementalOutput
    ) {

        public Parameters(LingJiOpenSourceChatOptions options) {
            this(
                    // spring chat model compatible
                    options.getResultFormat(),
                    options.getSeed(),
                    options.getMaxTokens(),
                    options.getTopP(),
                    options.getTopK(),
                    options.getRepetitionPenalty(),
                    options.getPresencePenalty(),
                    options.getTemperature(),
                    options.getStop(),
                    options.getIncrementalOutput()
            );
        }

    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
