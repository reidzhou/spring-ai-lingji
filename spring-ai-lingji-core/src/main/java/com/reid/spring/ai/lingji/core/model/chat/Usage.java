package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Usage(
        @JsonProperty(value = "output_tokens")
        Integer outputTokens,
        @JsonProperty(value = "input_tokens")
        Integer inputTokens,
        @JsonProperty(value = "total_tokens")
        Integer totalTokens
) {
}
