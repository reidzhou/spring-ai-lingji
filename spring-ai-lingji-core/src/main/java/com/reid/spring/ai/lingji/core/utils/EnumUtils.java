package com.reid.spring.ai.lingji.core.utils;

import com.reid.spring.ai.lingji.core.BaseEnum;
import com.reid.spring.ai.lingji.core.exception.LingJiException;

import java.util.Arrays;
import java.util.Optional;

public class EnumUtils {

    public static <T extends BaseEnum> Optional<T> getEnumByName(Class<T> enumClazz, String name) {
        if (enumClazz == null) {
            throw new IllegalArgumentException("enumClazz should not be null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("name should not be null.");
        }

        if (!enumClazz.isEnum()) {
            throw new IllegalArgumentException("enumClazz should be enum class.");
        }

        T[] enumArr = enumClazz.getEnumConstants();
        return Arrays.stream(enumArr).filter(t -> name.equals(t.getName())).findFirst();
    }

    public static <T extends BaseEnum> T getEnumByNameOrElseThrow(Class<T> enumClazz, String name, String errMsg) {
        return getEnumByName(enumClazz, name).orElseThrow(() -> new LingJiException(errMsg));
    }

}
