package com.reid.spring.ai.lingji.autoconfigure;

import com.reid.spring.ai.lingji.core.model.embeddings.LingJiEmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@AutoConfiguration(after = { SpringAiRetryAutoConfiguration.class })
@EnableConfigurationProperties({
        LingJiConnectionProperties.class,
        LingJiEmbeddingProperties.class
})
@ImportAutoConfiguration(classes = { SpringAiRetryAutoConfiguration.class })
public class LingJiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient(LingJiConnectionProperties connectionProperties) {
        ClientHttpRequestFactorySettings settings = new ClientHttpRequestFactorySettings(
                connectionProperties.getConnectTimeout(),
                connectionProperties.getReadTimeout(),
                (SslBundle) null
        );

        return RestClient
                .builder()
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

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
