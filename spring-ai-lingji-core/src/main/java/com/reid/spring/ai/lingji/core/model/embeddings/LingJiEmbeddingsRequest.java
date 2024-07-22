package com.reid.spring.ai.lingji.core.model.embeddings;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * see more https://help.aliyun.com/zh/dashscope/developer-reference/text-embedding-api-details?spm=a2c4g.11186623.0.0.3d6f5120MlKheI#63508c200bjrf
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiEmbeddingsRequest {

    @JsonProperty(value = "model")
    private String model;

    @JsonProperty(value = "input")
    private Input input = new Input();

    @JsonProperty(value = "parameters")
    private Parameters parameters = new Parameters();

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

    public static class Input {

        @JsonProperty(value = "texts")
        private List<String> texts = new ArrayList<>();

        public List<String> getTexts() {
            return texts;
        }

        public void setTexts(List<String> texts) {
            this.texts = texts;
        }
    }

    public static class Parameters {

        @JsonProperty(value = "text_type")
        private TextType textType = TextType.document;

        public TextType getTextType() {
            return textType;
        }

        public void setTextType(TextType textType) {
            this.textType = textType;
        }
    }

    public enum TextType {

        query, document

    }

}
