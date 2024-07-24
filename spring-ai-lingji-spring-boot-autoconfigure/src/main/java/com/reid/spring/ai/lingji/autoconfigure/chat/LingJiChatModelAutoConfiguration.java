package com.reid.spring.ai.lingji.autoconfigure.chat;

import com.reid.spring.ai.lingji.core.model.chat.LingJiChatModel;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@EnableConfigurationProperties({
        LingJiWebClientProperties.class,
        LingJiChatProperties.class
})
public class LingJiChatModelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiChatProperties.CONFIG_PREFIX, name = "enable", havingValue = "true")
    public WebClient webClient(LingJiWebClientProperties properties, @Nullable ClientHttpConnector clientHttpConnector) {
        return WebClient
                .builder()
                .clientConnector(clientHttpConnector)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiChatProperties.CONFIG_PREFIX, name = "enable", havingValue = "true")
    public FunctionCallbackContext springAiFunctionManager(ApplicationContext context) {
        FunctionCallbackContext functionCallbackContext = new FunctionCallbackContext();
        functionCallbackContext.setApplicationContext(context);
        return functionCallbackContext;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiChatProperties.CONFIG_PREFIX, name = "enable", havingValue = "true")
    public LingJiChatModel lingJiOpenSourceChatModel(
            LingJiChatProperties lingJiChatProperties,
            RestClient restClient,
            WebClient webClient,
            FunctionCallbackContext functionCallbackContext) {
        return new LingJiChatModel(
                lingJiChatProperties.getApiKey(),
                restClient,
                webClient,
                lingJiChatProperties.getOpenSourceOptions(),
                functionCallbackContext
        );
    }

}
