package com.reid.spring.ai.lingji.core.model.image;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LingJiImageOptions extends LingJiImageRuntimeOptions {

    @JsonProperty(value = "syncInterval")
    private Integer syncInterval = Constants.DEFAULT_SYNC_INTERVAL;

    @JsonProperty(value = "syncTimeout")
    private Integer syncTimeout = Constants.DEFAULT_SYNC_TIMEOUT;

    public Integer getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(Integer syncInterval) {
        this.syncInterval = syncInterval;
    }

    public Integer getSyncTimeout() {
        return syncTimeout;
    }

    public void setSyncTimeout(Integer syncTimeout) {
        this.syncTimeout = syncTimeout;
    }
}
