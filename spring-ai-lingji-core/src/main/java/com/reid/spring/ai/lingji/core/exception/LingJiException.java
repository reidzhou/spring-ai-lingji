package com.reid.spring.ai.lingji.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LingJiException extends RuntimeException {

    @JsonProperty(value = "http_code")
    private int httpCode;

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "request_id")
    private String requestId;

    public LingJiException(int httpCode) {
        this.httpCode = httpCode;
    }

    public LingJiException(int httpCode, String requestId, String code, String message) {
        this.httpCode = httpCode;
        this.requestId = requestId;
        this.code = code;
        this.message = message;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
