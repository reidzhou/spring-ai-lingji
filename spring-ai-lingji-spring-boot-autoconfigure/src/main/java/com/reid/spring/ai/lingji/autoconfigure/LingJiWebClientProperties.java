package com.reid.spring.ai.lingji.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(LingJiWebClientProperties.CONFIG_PREFIX)
public class LingJiWebClientProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.web.client";

}
