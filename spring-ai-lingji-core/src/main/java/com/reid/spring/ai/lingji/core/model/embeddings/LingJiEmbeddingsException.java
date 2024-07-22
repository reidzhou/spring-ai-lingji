package com.reid.spring.ai.lingji.core.model.embeddings;

import com.reid.spring.ai.lingji.core.exception.LingJiException;

public class LingJiEmbeddingsException extends LingJiException {

    public LingJiEmbeddingsException(int httpCode) {
        super(httpCode);
    }

    public LingJiEmbeddingsException(int httpCode, String requestId, String code, String message) {
        super(httpCode, requestId, code, message);
    }
}
