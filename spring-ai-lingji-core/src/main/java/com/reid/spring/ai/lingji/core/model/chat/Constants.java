package com.reid.spring.ai.lingji.core.model.chat;

public class Constants {

    public final static String HEADER_SSE = "X-DashScope-SSE";

    public final static String LING_JI_RESULT_FORMAT = "text";

    public final static String OPEN_AI_RESULT_FORMAT = "message";

    public final static String FINISH_REASON_NULL = "null";

    public final static String FINISH_REASON_STOP = "stop";

    public final static String FINISH_REASON_TOKEN = "token";

    public final static String FINISH_REASON_LENGTH = "length";

    public final static String TOOL_TYPE_FUNCTION = "function";

    public final static String LING_JI_CHAT_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public final static LingJiChatOptions DEFAULT_OPTIONS = new LingJiChatOptions();

}
