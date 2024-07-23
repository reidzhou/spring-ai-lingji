package com.reid.spring.ai.lingji.core.model.chat;

import com.reid.spring.ai.lingji.core.exception.LingJiException;

import java.util.Arrays;
import java.util.Optional;

public class Constants {

    public final static String HEADER_SSE = "X-DashScope-SSE";

    public final static String LING_JI_RESULT_FORMAT = "text";

    public final static String OPEN_AI_RESULT_FORMAT = "message";

    public final static String FINISH_REASON_NULL = "null";

    public final static String FINISH_REASON_STOP = "stop";

    public final static String FINISH_REASON_TOKEN = "token";

    public final static String LING_JI_CHAT_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public final static LingJiOpenSourceChatOptions DEFAULT_OPTIONS = new LingJiOpenSourceChatOptions();

    /**
     * see more https://help.aliyun.com/zh/dashscope/developer-reference/tongyi-qianwen-7b-14b-72b-api-detailes?spm=a2c4g.11186623.0.0.3d781b3fq82Rao#BQnl3
     */
    public enum OpenSourceModel {

        QWEN_2_57B("qwen2-57b-a14b-instruct"),
        QWEN_2_72B("qwen2-72b-instruct"),
        QWEN_2_7B("qwen2-7b-instruct"),
        QWEN_2_1_DOT_5B("qwen2-1.5b-instruct"),
        QWEN_2_0_DOT_57B("qwen2-0.5b-instruct"),
        QWEN_1_DOT_5_110B("qwen1.5-110b-chat"),
        QWEN_1_DOT_5_72B("qwen1.5-72b-chat"),
        QWEN_1_DOT_5_32B("qwen1.5-32b-chat"),
        QWEN_1_DOT_5_14B("qwen1.5-14b-chat"),
        QWEN_1_DOT_5_7B("qwen1.5-7b-chat"),
        QWEN_1_DOT_5_1_DOT_8B("qwen1.5-1.8b-chat"),
        QWEN_1_DOT_5_0_DOT_5B("qwen1.5-0.5b-chat"),
        CODE_QWEN_1_ODT_5_7B("codeqwen1.5-7b-chat"),
        QWEN_72B("qwen-72b-chat"),
        QWEN_14B("qwen-14b-chat"),
        QWEN_7B("qwen-7b-chat"),
        QWEN_1_DOT_8B_LONG("qwen-1.8b-longcontext-chat"),
        QWEN_1_DOT_8B("qwen-1.8b-chat"),
        ;

        private final String name;
        OpenSourceModel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static OpenSourceModel map(String name) {
            Optional<OpenSourceModel> optional = Arrays.stream(OpenSourceModel.values())
                    .filter(openSourceModel -> openSourceModel.getName().equals(name))
                    .findFirst();

            if (optional.isEmpty()) {
                throw new LingJiException(String.format("Model [name = %s] is illegal", name));
            }

            return optional.get();
        }

    }

}
