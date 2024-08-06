package com.reid.spring.ai.lingji.core.model.image;

import com.reid.spring.ai.lingji.core.BaseEnum;

public enum Model implements BaseEnum {

    WANX_V1("wanx-v1")
    ;

    private final String name;
    Model(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
