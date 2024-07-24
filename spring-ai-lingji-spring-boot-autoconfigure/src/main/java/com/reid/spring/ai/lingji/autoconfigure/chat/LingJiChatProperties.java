package com.reid.spring.ai.lingji.autoconfigure.chat;

import com.reid.spring.ai.lingji.autoconfigure.LingJiBaseProperties;
import com.reid.spring.ai.lingji.core.model.chat.LingJiChatOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(LingJiChatProperties.CONFIG_PREFIX)
public class LingJiChatProperties extends LingJiBaseProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.chat";

    private Boolean useOpenSourceModel = true;

    @NestedConfigurationProperty
    private LingJiChatOptions openSourceOptions = new LingJiChatOptions();


    public Boolean getUseOpenSourceModel() {
        return useOpenSourceModel;
    }

    public void setUseOpenSourceModel(Boolean useOpenSourceModel) {
        this.useOpenSourceModel = useOpenSourceModel;
    }

    public LingJiChatOptions getOpenSourceOptions() {
        return openSourceOptions;
    }

    public void setOpenSourceOptions(LingJiChatOptions openSourceOptions) {
        this.openSourceOptions = openSourceOptions;
    }
}
