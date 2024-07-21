package com.reid.spring.ai.lingji.core;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.tools.ToolCallBase;
import com.alibaba.dashscope.tools.ToolCallFunction;
import io.reactivex.Flowable;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.AbstractFunctionCallSupport;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LingJiChatModel extends AbstractFunctionCallSupport<Message, GenerationParam, GenerationResult>
        implements ChatModel, StreamingChatModel {

    private final Generation generation = null;

    private final RetryTemplate retryTemplate = null;

    private final LingJiChatOptions defaultOptions = null;

    protected LingJiChatModel(FunctionCallbackContext functionCallbackContext) {
        super(functionCallbackContext);
    }

    @Override
    public ChatResponse call(Prompt prompt) {

        GenerationParam generationParam = this.doCreateGenerationParam(prompt, false);

        return null;
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        GenerationParam generationParam = this.doCreateGenerationParam(prompt, false);

        return null;
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return this.defaultOptions;
    }

    @Override
    protected GenerationParam doCreateToolResponseRequest(
            GenerationParam previousRequest,
            Message responseMessage,
            List<Message> conversationHistory) {

        for (ToolCallBase toolCall : responseMessage.getToolCalls()) {

            String toolCallType = toolCall.getType();
            if (toolCallType.equals("function")) {

                ToolCallFunction toolCallFunction = (ToolCallFunction) toolCall;

                ToolCallFunction.CallFunction callFunction = toolCallFunction.getFunction();

                String functionName = callFunction.getName();
                String functionArguments = callFunction.getArguments();

                if (!this.functionCallbackRegister.containsKey(functionName)) {
                    throw new IllegalStateException("No function callback found for function name: " + functionName);
                }

                String functionResponse = this.functionCallbackRegister.get(functionName).call(functionArguments);

                conversationHistory.add(
                        Message
                                .builder()
                                .name(functionName)
                                .role("tool")
                                .content(functionResponse)
                                .toolCallId(toolCall.getId())
                                .build()
                );
            }
        }

        GenerationParam generationParam = GenerationParam.builder()
                .messages(conversationHistory)
                .build();

        generationParam = ModelOptionsUtils.merge(generationParam, previousRequest, GenerationParam.class);

        return generationParam;
    }

    @Override
    protected List<Message> doGetUserMessages(GenerationParam request) {
        return request.getMessages();
    }

    @Override
    protected Message doGetToolResponseMessage(GenerationResult response) {
        return response.getOutput().getChoices().iterator().next().getMessage();
    }

    @Override
    protected GenerationResult doChatCompletion(GenerationParam request) {
        try {
            return this.generation.call(request);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Flux<GenerationResult> doChatCompletionStream(GenerationParam request) {
        try {
            Flowable<GenerationResult> flowable = this.generation.streamCall(request);
            return Flux.from(flowable).map(generationResult -> generationResult);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean isToolFunctionCall(GenerationResult response) {
        GenerationOutput output = response.getOutput();
        if (output == null) {
            return false;
        }

        List<GenerationOutput.Choice> choices = output.getChoices();
        if (CollectionUtils.isEmpty(choices)) {
            return false;
        }

        GenerationOutput.Choice choice = choices.get(0);
        return !CollectionUtils.isEmpty(choice.getMessage().getToolCalls())
                && choice.getFinishReason().equals("tool_calls");
    }

    private GenerationParam doCreateGenerationParam(Prompt prompt, Boolean stream) {

        return GenerationParam.builder()
                .apiKey(this.defaultOptions.getApiKey())
                .securityCheck(this.defaultOptions.isSecurityCheck())
                .workspace(this.defaultOptions.getWorkspace())
                .resources(this.defaultOptions.getResources())
                .parameters(this.defaultOptions.getParameters())
                .headers(this.defaultOptions.getHeaders())
                .prompt(this.defaultOptions.getPrompt())
                .history(this.defaultOptions.getHistory())
                .messages(this.defaultOptions.getMessages())
                .maxLength(this.defaultOptions.getMaxLength())
                .maxTokens(this.defaultOptions.getMaxTokens())
                .topP(this.defaultOptions.getTopP().doubleValue())
                .topK(this.defaultOptions.getTopK())
                .enableSearch(this.defaultOptions.getEnableSearch())
                .seed(this.defaultOptions.getSeed())
                .resultFormat(this.defaultOptions.getResultFormat())
                .temperature(this.defaultOptions.getTemperature())
                .incrementalOutput(this.defaultOptions.getIncrementalOutput())
                .repetitionPenalty(this.defaultOptions.getRepetitionPenalty())
                .stopStrings(this.defaultOptions.getStopStrings())
                .stopTokens(this.defaultOptions.getStopTokens())
                .tools(this.defaultOptions.getTools())
                .toolChoice(this.defaultOptions.toolChoice)
                .build();

    }
}
