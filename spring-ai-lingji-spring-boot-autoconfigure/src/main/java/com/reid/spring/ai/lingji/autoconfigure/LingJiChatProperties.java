package com.reid.spring.ai.lingji.autoconfigure;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.protocol.ConnectionOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(LingJiChatProperties.CONFIG_PREFIX)
public class LingJiChatProperties extends LingJiBaseProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.chat";

    public static final String DEFAULT_CHAT_MODEL = TextEmbedding.Models.TEXT_EMBEDDING_V1;

    @NestedConfigurationProperty
    private ConnectionOptions connection = ConnectionOptions.builder().build();

    public ConnectionOptions getConnection() {
        return connection;
    }

    public void setConnection(ConnectionOptions connection) {
        this.connection = connection;
    }
}
