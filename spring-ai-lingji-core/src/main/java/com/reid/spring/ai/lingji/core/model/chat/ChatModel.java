package com.reid.spring.ai.lingji.core.model.chat;

import com.reid.spring.ai.lingji.core.exception.LingJiException;

import java.util.Arrays;
import java.util.Optional;

public enum ChatModel {

    QWEN_TURBO("qwen-turbo", true),
    QWEN_TURBO_0624("qwen-turbo-0624", true),
    QWEN_TURBO_0206("qwen-turbo-0206", true),
    QWEN_PLUS("qwen-plus", true),
    QWEN_PLUS_0624("qwen-plus-0624", true),
    QWEN_PLUS_0206("qwen-plus-0206", true),
    QWEN_MAX("qwen-max", true),
    QWEN_MAX_0428("qwen-max-0428", true),
    QWEN_MAX_0403("qwen-max-0403", true),
    QWEN_MAX_0107("qwen-max-0107", true),
    QWEN_MAX_1201("qwen-max-1201", true),
    QWEN_MAX_LONG_CONTEXT("qwen-max-longcontext", true),

    // open source model
    OPEN_SOURCE_QWEN_2_57B("qwen2-57b-a14b-instruct", false),
    OPEN_SOURCE_QWEN_2_72B("qwen2-72b-instruct", false),
    OPEN_SOURCE_QWEN_2_7B("qwen2-7b-instruct", false),
    OPEN_SOURCE_QWEN_2_1_DOT_5B("qwen2-1.5b-instruct", false),
    OPEN_SOURCE_QWEN_2_0_DOT_57B("qwen2-0.5b-instruct", false),
    OPEN_SOURCE_QWEN_1_DOT_5_110B("qwen1.5-110b-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_5_72B("qwen1.5-72b-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_5_32B("qwen1.5-32b-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_5_14B("qwen1.5-14b-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_5_7B("qwen1.5-7b-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_5_1_DOT_8B("qwen1.5-1.8b-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_5_0_DOT_5B("qwen1.5-0.5b-chat", false),
    OPEN_SOURCE_CODE_QWEN_1_ODT_5_7B("codeqwen1.5-7b-chat", false),
    OPEN_SOURCE_QWEN_72B("qwen-72b-chat", false),
    OPEN_SOURCE_QWEN_14B("qwen-14b-chat", false),
    OPEN_SOURCE_QWEN_7B("qwen-7b-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_8B_LONG("qwen-1.8b-longcontext-chat", false),
    OPEN_SOURCE_QWEN_1_DOT_8B("qwen-1.8b-chat", false),
    ;

    private final String name;
    private final Boolean supportFunctionCalling;
    ChatModel(String name, Boolean supportFunctionCalling) {
        this.name = name;
        this.supportFunctionCalling = supportFunctionCalling;
    }

    public String getName() {
        return name;
    }

    public Boolean getSupportFunctionCalling() {
        return supportFunctionCalling;
    }

    public static ChatModel map(String name) {
        Optional<ChatModel> optional = Arrays.stream(ChatModel.values())
                .filter(model -> model.getName().equals(name))
                .findFirst();

        if (optional.isEmpty()) {
            throw new LingJiException(String.format("Model [name = %s] is illegal", name));
        }

        return optional.get();
    }
}
