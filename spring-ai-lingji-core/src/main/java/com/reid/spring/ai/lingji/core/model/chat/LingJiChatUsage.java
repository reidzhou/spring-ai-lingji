package com.reid.spring.ai.lingji.core.model.chat;

import org.springframework.ai.chat.metadata.Usage;

public class LingJiChatUsage implements Usage {

    private final com.reid.spring.ai.lingji.core.model.chat.Usage usage;

    public LingJiChatUsage(com.reid.spring.ai.lingji.core.model.chat.Usage usage) {
        this.usage = usage;
    }

    @Override
    public Long getPromptTokens() {
        return Long.valueOf(this.usage.inputTokens());
    }

    @Override
    public Long getGenerationTokens() {
        return Long.valueOf(this.usage.outputTokens());
    }

    @Override
    public Long getTotalTokens() {
        Integer totalTokens = this.usage.totalTokens();
        return totalTokens == null ? Usage.super.getTotalTokens() : Long.valueOf(totalTokens);
    }

}
