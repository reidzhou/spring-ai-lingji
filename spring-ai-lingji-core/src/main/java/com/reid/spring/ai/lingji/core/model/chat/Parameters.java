package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Parameters(
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
        @JsonProperty(value = "enable_search")
        Boolean enableSearch,
        @JsonProperty(value = "incremental_output")
        Boolean incrementalOutput,
        @JsonProperty(value = "tools")
        List<Tool> tools,
        // none or auto or ToolChoice object
        @JsonProperty(value = "tool_choice")
        Object toolChoice
) {

    public Parameters(LingJiChatOptions options) {
        this(
                options.getResultFormat(),
                options.getSeed(),
                options.getMaxTokens(),
                options.getTopP(),
                options.getTopK(),
                options.getRepetitionPenalty(),
                options.getPresencePenalty(),
                options.getTemperature(),
                options.getStop(),
                options.getEnableSearch(),
                options.getIncrementalOutput(),
                options.getTools(),
                options.getToolChoice()
        );
    }

    public record Tool(
            @JsonProperty(value = "type")
            String type,
            @JsonProperty(value = "function")
            Tool.Function function
    ) {
        public Tool(Tool.Function function) {
            this("function", function);
        }

        public record Function(
                @JsonProperty(value = "name")
                String name,
                @JsonProperty(value = "description")
                String description,
                // json schema object
                @JsonProperty(value = "parameters")
                Object parameters
        ) {}
    }

}
