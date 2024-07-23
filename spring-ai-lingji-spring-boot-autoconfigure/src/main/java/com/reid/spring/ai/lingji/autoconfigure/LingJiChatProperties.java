package com.reid.spring.ai.lingji.autoconfigure;

import com.reid.spring.ai.lingji.core.model.chat.LingJiOpenSourceChatOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(LingJiChatProperties.CONFIG_PREFIX)
public class LingJiChatProperties extends LingJiBaseProperties {

    public static final String CONFIG_PREFIX = "spring.ai.lingji.chat";

    private Boolean useOpenSourceModel = true;

    @NestedConfigurationProperty
    private LingJiOpenSourceChatOptions openSourceOptions = new LingJiOpenSourceChatOptions();

//    @NestedConfigurationProperty
//    private LingJiOpenSourceChatOptions closedSourceOptions = new LingJiOpenSourceChatOptions();


    public Boolean getUseOpenSourceModel() {
        return useOpenSourceModel;
    }

    public void setUseOpenSourceModel(Boolean useOpenSourceModel) {
        this.useOpenSourceModel = useOpenSourceModel;
    }

    public LingJiOpenSourceChatOptions getOpenSourceOptions() {
        return openSourceOptions;
    }

    public void setOpenSourceOptions(LingJiOpenSourceChatOptions openSourceOptions) {
        this.openSourceOptions = openSourceOptions;
    }
}
