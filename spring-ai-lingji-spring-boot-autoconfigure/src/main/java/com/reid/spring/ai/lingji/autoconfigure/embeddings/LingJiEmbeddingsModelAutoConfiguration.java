package com.reid.spring.ai.lingji.autoconfigure.embeddings;

import com.reid.spring.ai.lingji.core.model.embeddings.LingJiEmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties({LingJiEmbeddingProperties.class})
public class LingJiEmbeddingsModelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiEmbeddingProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
    public LingJiEmbeddingModel lingJiEmbeddingModel(
            LingJiEmbeddingProperties lingJiEmbeddingProperties,
            RestClient restClient) {
        return new LingJiEmbeddingModel(
                lingJiEmbeddingProperties.getApiKey(),
                restClient,
                lingJiEmbeddingProperties.getMetadataMode(),
                lingJiEmbeddingProperties.getOptions()
        );
    }

}
