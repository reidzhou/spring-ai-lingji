package com.reid.spring.ai.lingji.core.model.embeddings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reid.spring.ai.lingji.core.common.LingJiRPCCommonResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * see more https://help.aliyun.com/zh/dashscope/developer-reference/text-embedding-api-details?spm=a2c4g.11186623.0.0.3d6f5120MlKheI#eaf22810ferku
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiEmbeddingsResponse extends LingJiRPCCommonResponse {

    @JsonProperty(value = "output")
    private Output output = new Output();

    @JsonProperty(value = "usage")
    private Usage usage = new Usage();

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

    public static class Output {

        @JsonProperty(value = "embeddings")
        private List<Embedding> embeddings = new ArrayList<>();

        public List<Embedding> getEmbeddings() {
            return embeddings;
        }

        public void setEmbeddings(List<Embedding> embeddings) {
            this.embeddings = embeddings;
        }
    }

    public static class Embedding {

        @JsonProperty(value = "text_index")
        private Integer textIndex;

        @JsonProperty(value = "embedding")
        private List<Double> embedding = new ArrayList<>();

        public Integer getTextIndex() {
            return textIndex;
        }

        public void setTextIndex(Integer textIndex) {
            this.textIndex = textIndex;
        }

        public List<Double> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Double> embedding) {
            this.embedding = embedding;
        }
    }

    public static class Usage {

        @JsonProperty(value = "total_tokens")
        private Integer totalTokens;

        public Integer getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }

    }

}
