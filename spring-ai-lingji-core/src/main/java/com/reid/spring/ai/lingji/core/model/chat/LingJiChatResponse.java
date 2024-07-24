package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reid.spring.ai.lingji.core.common.LingJiRPCCommonResponse;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiChatResponse extends LingJiRPCCommonResponse {

    @JsonProperty(value = "output")
    private Output output;

    @JsonProperty(value = "usage")
    private Usage usage;

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}
