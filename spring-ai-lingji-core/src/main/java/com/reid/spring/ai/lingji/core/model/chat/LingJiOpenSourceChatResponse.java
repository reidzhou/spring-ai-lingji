package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reid.spring.ai.lingji.core.common.LingJiRPCCommonResponse;

import java.util.List;

/**
 * see more https://help.aliyun.com/zh/dashscope/developer-reference/tongyi-qianwen-7b-14b-72b-api-detailes?spm=a2c4g.11186623.0.0.3d781b3fq82Rao#25745d61fbx49
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiOpenSourceChatResponse extends LingJiRPCCommonResponse {

    @JsonProperty(value = "output")
    private Output output;

    @JsonProperty(value = "usage")
    private Usage usage;

    public record Output(
            @JsonProperty(value = "text")
            String text,
            @JsonProperty(value = "finish_reason")
            String finishReason,
            @JsonProperty(value = "choices")
            List<Choice> choices

    ) {}

    public record Choice(
            @JsonProperty(value = "finish_reason")
            String finishReason,
            @JsonProperty(value = "message")
            Message message

    ) {}

    public record Message(
            @JsonProperty(value = "role")
            Role role,
            @JsonProperty(value = "content")
            String content
    ) {}

    public record Usage(
            @JsonProperty(value = "output_tokens")
            Integer outputTokens,
            @JsonProperty(value = "input_tokens")
            Integer inputTokens
    ) {}

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}
