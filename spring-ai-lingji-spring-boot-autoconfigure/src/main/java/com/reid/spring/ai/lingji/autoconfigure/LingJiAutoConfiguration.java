package com.reid.spring.ai.lingji.autoconfigure;

import com.reid.spring.ai.lingji.core.model.chat.LingJiOpenSourceChatModel;
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
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration(after = { SpringAiRetryAutoConfiguration.class })
@EnableConfigurationProperties({
        LingJiRestClientProperties.class,
        LingJiWebClientProperties.class,
        LingJiEmbeddingProperties.class,
        LingJiChatProperties.class
})
@ImportAutoConfiguration(classes = { SpringAiRetryAutoConfiguration.class })
public class LingJiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient(LingJiRestClientProperties properties) {
        ClientHttpRequestFactorySettings settings = new ClientHttpRequestFactorySettings(
                properties.getConnectTimeout(),
                properties.getReadTimeout(),
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

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(LingJiWebClientProperties properties, @Nullable ClientHttpConnector clientHttpConnector) {
        return WebClient
                .builder()
                .clientConnector(clientHttpConnector)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiChatProperties.CONFIG_PREFIX, name = "useOpenSourceModel", havingValue = "true")
    public LingJiOpenSourceChatModel lingJiOpenSourceChatModel(
            LingJiChatProperties lingJiChatProperties,
            RestClient restClient,
            WebClient webClient) {
        return new LingJiOpenSourceChatModel(
                lingJiChatProperties.getApiKey(),
                restClient,
                webClient,
                lingJiChatProperties.getOpenSourceOptions()
        );
    }

}
