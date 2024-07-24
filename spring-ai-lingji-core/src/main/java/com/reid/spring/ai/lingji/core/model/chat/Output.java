package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Output(
        @Deprecated
        @JsonProperty(value = "text")
        String text,
        @Deprecated
        @JsonProperty(value = "finish_reason")
        String finishReason,
        @JsonProperty(value = "choices")
        List<Choice> choices
) {

    public record Choice(
            @JsonProperty(value = "finish_reason")
            String finishReason,
            @JsonProperty(value = "message")
            Message message
    ) {}

}
