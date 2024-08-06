package com.reid.spring.ai.lingji.core.model.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reid.spring.ai.lingji.core.utils.EnumUtils;
import org.springframework.ai.image.ImageOptions;

public class LingJiImageRuntimeOptions implements ImageOptions {

    @JsonProperty(value = "model")
    private String model = Model.WANX_V1.getName();

    @JsonProperty(value = "style")
    private String style = Constants.Style.AUTO.getName();

    @JsonProperty(value = "size")
    private String size = Constants.Size.RESOLUTION_1024_1024.getName();

    @JsonProperty(value = "n")
    private Integer n = 1;

    @JsonProperty(value = "seed")
    private Long seed;

    @JsonProperty(value = "ref_strength")
    private Float refStrength;

    @JsonProperty(value = "ref_mode")
    private String refMode = Constants.RefMode.REPAINT.getName();

    @Override
    public Integer getN() {
        return this.n;
    }

    @Override
    public String getModel() {
        return this.model;
    }

    @Override
    public Integer getWidth() {
        return Integer.valueOf(this.size.substring(0, this.size.indexOf("0")));
    }

    @Override
    public Integer getHeight() {
        return Integer.valueOf(this.size.substring(this.size.indexOf("0") + 1));
    }

    @Override
    public String getResponseFormat() {
        return "url";
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        EnumUtils.getEnumByNameOrElseThrow(Constants.Style.class, style, "unknown style value [" + style + "]");
        this.style = style;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        EnumUtils.getEnumByNameOrElseThrow(Constants.Size.class, size, "unknown size value [" + size + "]");
        this.size = size;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public Float getRefStrength() {
        return refStrength;
    }

    public void setRefStrength(Float refStrength) {
        this.refStrength = refStrength;
    }

    public String getRefMode() {
        return refMode;
    }

    public void setRefMode(String refMode) {
        EnumUtils.getEnumByNameOrElseThrow(Constants.RefMode.class, refMode, "unknown ref_mode value [" + refMode + "]");
        this.refMode = refMode;
    }
}
