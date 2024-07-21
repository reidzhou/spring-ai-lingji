package com.reid.spring.ai.lingji.autoconfigure;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.protocol.Protocol;
import com.reid.spring.ai.lingji.core.LingJiChatModel;
import com.reid.spring.ai.lingji.core.LingJiEmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;

@AutoConfiguration(after = { SpringAiRetryAutoConfiguration.class })
@EnableConfigurationProperties({ LingJiEmbeddingProperties.class, LingJiChatProperties.class })
@ImportAutoConfiguration(classes = { SpringAiRetryAutoConfiguration.class })
public class LingJiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiEmbeddingProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
    public TextEmbedding textEmbedding(LingJiEmbeddingProperties lingJiEmbeddingProperties) {
        return new TextEmbedding(lingJiEmbeddingProperties.getBaseUrl());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiEmbeddingProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
    public LingJiEmbeddingModel lingJiEmbeddingModel(
            LingJiEmbeddingProperties lingJiEmbeddingProperties,
            TextEmbedding textEmbedding,
            RetryTemplate retryTemplate) {
        return new LingJiEmbeddingModel(
                lingJiEmbeddingProperties.getApiKey(),
                textEmbedding,
                lingJiEmbeddingProperties.getMetadataMode(),
                retryTemplate
        );
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
    public Generation generation(LingJiChatProperties lingJiChatProperties) {
        return new Generation(
                Protocol.HTTP.getValue(),
                lingJiChatProperties.getBaseUrl(),
                lingJiChatProperties.getConnection()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LingJiChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
    public LingJiChatModel lingJiChatModel() {
        return null;
    }

//	@Bean
//	@ConditionalOnMissingBean
//	public AnthropicApi anthropicApi(AnthropicConnectionProperties connectionProperties,
//			RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {
//
//		return new AnthropicApi(connectionProperties.getBaseUrl(), connectionProperties.getApiKey(),
//				connectionProperties.getVersion(), restClientBuilder, responseErrorHandler,
//				connectionProperties.getBetaVersion());
//	}
//
//	@Bean
//	@ConditionalOnMissingBean
//	public AnthropicChatModel anthropicChatModel(AnthropicApi anthropicApi, AnthropicChatProperties chatProperties,
//			RetryTemplate retryTemplate, FunctionCallbackContext functionCallbackContext,
//			List<FunctionCallback> toolFunctionCallbacks) {
//
//		if (!CollectionUtils.isEmpty(toolFunctionCallbacks)) {
//			chatProperties.getOptions().getFunctionCallbacks().addAll(toolFunctionCallbacks);
//		}
//
//		return new AnthropicChatModel(anthropicApi, chatProperties.getOptions(), retryTemplate,
//				functionCallbackContext);
//	}
//
//	@Bean
//	@ConditionalOnMissingBean
//	public FunctionCallbackContext springAiFunctionManager(ApplicationContext context) {
//		FunctionCallbackContext manager = new FunctionCallbackContext();
//		manager.setApplicationContext(context);
//		return manager;
//	}

}
