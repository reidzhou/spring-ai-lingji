package com.reid.spring.ai.lingji.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallingOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiChatOptions implements FunctionCallingOptions, ChatOptions {

    @JsonProperty(value = "model")
    private String model = ChatModel.QWEN_TURBO.getName();
    @JsonProperty(value = "result_format")
    private String resultFormat = Constants.OPEN_AI_RESULT_FORMAT;
    @JsonProperty(value = "seed")
    private Integer seed = 1234;
    @JsonProperty(value = "max_tokens")
    private Integer maxTokens = 1500;
    @JsonProperty(value = "top_p")
    private Float topP = 0.8f;
    @JsonProperty(value = "top_k")
    private Integer topK;
    @JsonProperty(value = "repetition_penalty")
    private Float repetitionPenalty = 1f;
    @JsonProperty(value = "presence_penalty")
    private Float presencePenalty;
    @JsonProperty(value = "temperature")
    private Float temperature = 0.85f;

    // todo improve
    // multi modal type:
    // 1. String,               eg: "stop"
    // 2. String List,          eg: ["stop"]
    // 3. Integer List,         eg: [123, 123]
    // 4. Nested Integer List,  eg: [[123, 123]]
    @JsonProperty(value = "stop")
    private Object stop;
    @JsonProperty(value = "incremental_output")
    private Boolean incrementalOutput = false;
    @JsonProperty(value = "enable_search")
    private Boolean enableSearch = false;
    @JsonProperty(value = "tools")
    private List<Parameters.Tool> tools;
    // none or auto or ToolChoice object
    @JsonProperty(value = "tool_choice")
    private Object toolChoice;

    private List<FunctionCallback> functionCallbacks = new ArrayList<>();

    private Set<String> functions = new HashSet<>();


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public Float getTopP() {
        return topP;
    }

    public void setTopP(Float topP) {
        this.topP = topP;
    }

    @Override
    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Float getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(Float repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public Float getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Float presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    @Override
    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Object getStop() {
        return stop;
    }

    public void setStop(Object stop) {
        this.stop = stop;
    }

    public Boolean getIncrementalOutput() {
        return incrementalOutput;
    }

    public void setIncrementalOutput(Boolean incrementalOutput) {
        this.incrementalOutput = incrementalOutput;
    }

    public Boolean getEnableSearch() {
        return enableSearch;
    }

    public void setEnableSearch(Boolean enableSearch) {
        this.enableSearch = enableSearch;
    }

    public List<Parameters.Tool> getTools() {
        return tools;
    }

    public void setTools(List<Parameters.Tool> tools) {
        this.tools = tools;
    }

    public Object getToolChoice() {
        return toolChoice;
    }

    public void setToolChoice(Object toolChoice) {
        this.toolChoice = toolChoice;
    }

    @Override
    public List<FunctionCallback> getFunctionCallbacks() {
        return this.functionCallbacks;
    }

    @Override
    public void setFunctionCallbacks(List<FunctionCallback> functionCallbacks) {
        this.functionCallbacks = functionCallbacks;
    }

    @Override
    public Set<String> getFunctions() {
        return this.functions;
    }

    @Override
    public void setFunctions(Set<String> functions) {
        this.functions = functions;
    }
}
