package com.reid.spring.ai.lingji.core.exception;

public class LingJiException extends RuntimeException {

    public LingJiException() {}

    public LingJiException(String msg) {
        super(msg);
    }

    public LingJiException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
