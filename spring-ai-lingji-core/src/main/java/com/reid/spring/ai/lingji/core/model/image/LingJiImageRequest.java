package com.reid.spring.ai.lingji.core.model.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiImageRequest {

    @JsonProperty(value = "model")
    private String model;

    @JsonProperty(value = "input")
    private Input input;

    @JsonProperty(value = "parameters")
    private Parameters parameters;

    public static class Input {

        @JsonProperty(value = "prompt")
        private String prompt;

        @JsonProperty(value = "negative_prompt")
        private String negativePrompt;

        @JsonProperty(value = "ref_img")
        private String refImg;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getNegativePrompt() {
            return negativePrompt;
        }

        public void setNegativePrompt(String negativePrompt) {
            this.negativePrompt = negativePrompt;
        }

        public String getRefImg() {
            return refImg;
        }

        public void setRefImg(String refImg) {
            this.refImg = refImg;
        }
    }

    public static class Parameters {

        @JsonProperty(value = "style")
        private String style;

        @JsonProperty(value = "size")
        private String size;

        @JsonProperty(value = "n")
        private Integer n;

        @JsonProperty(value = "seed")
        private Long seed;

        @JsonProperty(value = "ref_strength")
        private Float refStrength;

        @JsonProperty(value = "ref_mode")
        private String refMode;

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public Integer getN() {
            return n;
        }

        public void setN(Integer n) {
            this.n = n;
        }

        public Long getSeed() {
            return seed;
        }

        public void setSeed(Long seed) {
            this.seed = seed;
        }

        public Float getRefStrength() {
            return refStrength;
        }

        public void setRefStrength(Float refStrength) {
            this.refStrength = refStrength;
        }

        public String getRefMode() {
            return refMode;
        }

        public void setRefMode(String refMode) {
            this.refMode = refMode;
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
