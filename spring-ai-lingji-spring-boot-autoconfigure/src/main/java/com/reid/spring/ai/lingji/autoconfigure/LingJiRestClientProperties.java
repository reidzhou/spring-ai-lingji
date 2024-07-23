package com.reid.spring.ai.lingji.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(LingJiRestClientProperties.CONFIG_PREFIX)
public class LingJiRestClientProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.rest.client";

    private Duration connectTimeout;

    private Duration readTimeout;

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }
}
