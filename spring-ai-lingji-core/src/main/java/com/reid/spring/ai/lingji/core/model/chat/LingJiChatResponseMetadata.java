package com.reid.spring.ai.lingji.core.model.chat;

import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;

import java.util.HashMap;

public class LingJiChatResponseMetadata extends HashMap<String, Object> implements ChatResponseMetadata {

    private final LingJiChatUsage usage;

    public LingJiChatResponseMetadata(LingJiChatUsage usage) {
        this.usage = usage;
    }

    @Override
    public Usage getUsage() {
        return this.usage;
    }

}
