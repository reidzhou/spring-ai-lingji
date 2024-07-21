package com.reid.spring.ai.lingji.core;

import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.History;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.tools.ToolBase;
import com.alibaba.dashscope.utils.GsonExclude;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Singular;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.util.Assert;

import java.util.*;

public class LingJiChatOptions implements FunctionCallingOptions, ChatOptions {

    private String apiKey;

    private boolean securityCheck = false;

    private String workspace;

    private Object resources = null;

    protected Map<String, Object> parameters;

    protected Map<String, Object> headers;

    private String prompt;

    private List<History> history;

    private List<Message> messages;

    private Integer maxLength;

    private Double topP;

    private Integer topK;

    private Boolean enableSearch = false;

    private Integer seed;

    private String resultFormat = GenerationParam.ResultFormat.TEXT;

    private Float temperature;

    private Boolean incrementalOutput = false;

    private Integer maxTokens;

    private Float repetitionPenalty;

    private List<String> stopStrings;

    private List<List<Integer>> stopTokens;

    private List<ToolBase> tools;

    protected Object toolChoice;

    private List<FunctionCallback> functionCallbacks = new ArrayList<>();

    private Set<String> functions = new HashSet<>();

    @Override
    public Float getTemperature() {
        return this.temperature;
    }

    @Override
    public Float getTopP() {
        return this.topP.floatValue();
    }

    @Override
    public Integer getTopK() {
        return this.topK;
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isSecurityCheck() {
        return securityCheck;
    }

    public void setSecurityCheck(boolean securityCheck) {
        this.securityCheck = securityCheck;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public Object getResources() {
        return resources;
    }

    public void setResources(Object resources) {
        this.resources = resources;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Boolean getEnableSearch() {
        return enableSearch;
    }

    public void setEnableSearch(Boolean enableSearch) {
        this.enableSearch = enableSearch;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Boolean getIncrementalOutput() {
        return incrementalOutput;
    }

    public void setIncrementalOutput(Boolean incrementalOutput) {
        this.incrementalOutput = incrementalOutput;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Float getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(Float repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public List<String> getStopStrings() {
        return stopStrings;
    }

    public void setStopStrings(List<String> stopStrings) {
        this.stopStrings = stopStrings;
    }

    public List<List<Integer>> getStopTokens() {
        return stopTokens;
    }

    public void setStopTokens(List<List<Integer>> stopTokens) {
        this.stopTokens = stopTokens;
    }

    public List<ToolBase> getTools() {
        return tools;
    }

    public void setTools(List<ToolBase> tools) {
        this.tools = tools;
    }

    public Object getToolChoice() {
        return toolChoice;
    }

    public void setToolChoice(Object toolChoice) {
        this.toolChoice = toolChoice;
    }
}
