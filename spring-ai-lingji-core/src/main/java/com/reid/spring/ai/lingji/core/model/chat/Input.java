package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Deprecated
    public record History(
            @JsonProperty(value = "user")
            String user,
            @JsonProperty(value = "bot")
            String bot
    ) {}

}
