package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Message(
        @JsonProperty(value = "role")
        Role role,
        @JsonProperty(value = "content")
        String content,
        @JsonProperty(value = "name")
        String name,
        @JsonProperty(value = "tool_calls")
        List<ToolCall> toolCalls
) {

    public Message(Role role, String content) {
        this(role, content, null, null);
    }

    public Message(Role role, String content, String name) {
        this(role, content, name, null);
    }

    public record ToolCall(
            @JsonProperty(value = "type")
            String type,
            @JsonProperty(value = "function")
            Function function
    ) {

        public ToolCall(Function function) {
            this(Constants.TOOL_TYPE_FUNCTION, function);
        }

        public record Function(
                @JsonProperty(value = "name")
                String name,
                @JsonProperty(value = "arguments")
                String arguments
        ) {}

    }
}
