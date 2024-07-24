package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * see more https://help.aliyun.com/zh/dashscope/developer-reference/use-qwen?spm=a2c4g.11186623.0.0.59ce1f5aJbh2Tu#f09300c38f4nz
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiChatRequest {

    @JsonProperty(value = "model")
    private String model;

    @JsonProperty(value = "input")
    private Input input;

    @JsonProperty(value = "parameters")
    private Parameters parameters;

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
