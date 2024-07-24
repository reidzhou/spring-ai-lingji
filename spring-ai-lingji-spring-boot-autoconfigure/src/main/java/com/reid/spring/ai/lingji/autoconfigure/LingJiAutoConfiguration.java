package com.reid.spring.ai.lingji.autoconfigure;

import com.reid.spring.ai.lingji.autoconfigure.chat.LingJiChatModelAutoConfiguration;
import com.reid.spring.ai.lingji.autoconfigure.embeddings.LingJiEmbeddingsModelAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties({LingJiRestClientProperties.class,})
@ImportAutoConfiguration(value = {
        LingJiEmbeddingsModelAutoConfiguration.class,
        LingJiChatModelAutoConfiguration.class
})
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

}
