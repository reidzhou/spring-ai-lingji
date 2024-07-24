package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ToolChoice(
        @JsonProperty(value = "type")
        String type,
        @JsonProperty(value = "function")
        Function function

) {

    public ToolChoice(Function function) {
        this("function", function);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Function(
            @JsonProperty(value = "name")
            String name
    ) {}

}
