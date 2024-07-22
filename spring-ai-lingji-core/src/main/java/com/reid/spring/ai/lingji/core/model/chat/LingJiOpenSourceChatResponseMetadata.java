package com.reid.spring.ai.lingji.core.model.chat;

import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;

import java.util.HashMap;

public class LingJiOpenSourceChatResponseMetadata extends HashMap<String, Object> implements ChatResponseMetadata {

    private final LingJiOpenSourceChatUsage usage;

    public LingJiOpenSourceChatResponseMetadata(LingJiOpenSourceChatUsage usage) {
        this.usage = usage;
    }

    @Override
    public Usage getUsage() {
        return this.usage;
    }

    public static class LingJiOpenSourceChatUsage implements Usage {

        private final LingJiOpenSourceChatResponse.Usage usage;

        public LingJiOpenSourceChatUsage(LingJiOpenSourceChatResponse.Usage usage) {
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
    }
}
