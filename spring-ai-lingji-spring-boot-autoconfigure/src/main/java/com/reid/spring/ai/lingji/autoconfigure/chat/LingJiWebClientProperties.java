package com.reid.spring.ai.lingji.autoconfigure.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(LingJiWebClientProperties.CONFIG_PREFIX)
public class LingJiWebClientProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.chat.web.client";

}
