package com.reid.spring.ai.lingji.autoconfigure;

import com.reid.spring.ai.lingji.core.model.embeddings.Constants;
import com.reid.spring.ai.lingji.core.model.embeddings.LingJiEmbeddingOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(LingJiEmbeddingProperties.CONFIG_PREFIX)
public class LingJiEmbeddingProperties extends LingJiBaseProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.embedding";

    private MetadataMode metadataMode = MetadataMode.EMBED;

    @NestedConfigurationProperty
    private LingJiEmbeddingOptions options = LingJiEmbeddingOptions.builder()
            .withModel(Constants.LING_JI_EMBEDDINGS_V1)
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
