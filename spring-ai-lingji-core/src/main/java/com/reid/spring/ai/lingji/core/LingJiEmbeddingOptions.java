package com.reid.spring.ai.lingji.core;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.util.Assert;

public class LingJiEmbeddingOptions implements EmbeddingOptions {

    private String model;

    public LingJiEmbeddingOptions() {}

    public LingJiEmbeddingOptions(String model) {
        this.doCheckModel(model);
        this.model = model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        protected LingJiEmbeddingOptions options;

        public Builder() {
            this.options = new LingJiEmbeddingOptions();
        }

        public Builder withModel(String model) {
            this.options.doCheckModel(model);
            this.options.setModel(model);
            return this;
        }

        public LingJiEmbeddingOptions build() {
            return this.options;
        }

    }

    private void doCheckModel(String model) {
        Assert.notNull(model, "The model can not be null.");
        Assert.isTrue(
                TextEmbedding.Models.TEXT_EMBEDDING_V1.equals(model)
                        || TextEmbedding.Models.TEXT_EMBEDDING_V2.equals(model),
                String.format(
                        "The embedding model type is illegal, must be %s or %s",
                        TextEmbedding.Models.TEXT_EMBEDDING_V1,
                        TextEmbedding.Models.TEXT_EMBEDDING_V2
                )
        );
    }

}
