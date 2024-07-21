package com.reid.spring.ai.lingji.autoconfigure;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.reid.spring.ai.lingji.core.LingJiEmbeddingOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(LingJiEmbeddingProperties.CONFIG_PREFIX)
public class LingJiEmbeddingProperties extends LingJiBaseProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.embedding";

    public static final String DEFAULT_EMBEDDING_MODEL = TextEmbedding.Models.TEXT_EMBEDDING_V1;

    private MetadataMode metadataMode = MetadataMode.EMBED;

    @NestedConfigurationProperty
    private LingJiEmbeddingOptions options = LingJiEmbeddingOptions.builder()
            .withModel(DEFAULT_EMBEDDING_MODEL)
            .build();

    public MetadataMode getMetadataMode() {
        return metadataMode;
    }

    public void setMetadataMode(MetadataMode metadataMode) {
        this.metadataMode = metadataMode;
    }

    public LingJiEmbeddingOptions getOptions() {
        return options;
    }

    public void setOptions(LingJiEmbeddingOptions options) {
        this.options = options;
    }
}
