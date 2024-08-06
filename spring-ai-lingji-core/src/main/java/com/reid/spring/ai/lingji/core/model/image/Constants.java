package com.reid.spring.ai.lingji.core.model.image;

import com.reid.spring.ai.lingji.core.BaseEnum;

public class Constants {

    public static final String BASE_URL = "https://dashscope.aliyuncs.com";

    public static final String TASK_CREATE_URI = "/api/v1/services/aigc/text2image/image-synthesis";

    public static final String TASK_FETCH_URI = "/api/v1/tasks/%s";

    public static final Integer DEFAULT_SYNC_INTERVAL = 2;

    public static final Integer DEFAULT_SYNC_TIMEOUT = 10;

    public static final LingJiImageOptions DEFAULT_OPTIONS = new LingJiImageOptions();

    public enum Style implements BaseEnum {

        PHOTOGRAPHY("<photography>"),
        PORTRAIT("<portrait>"),
        THREE_D_CARTOON("<3d cartoon>"),
        ANIME("<anime>"),
        OIL_PAINTING("<oil painting>"),
        WATERCOLOR("<watercolor>"),
        SKETCH("<sketch>"),
        CHINESE_PAINTING("<chinese painting>"),
        FLAT_ILLUSTRATION("<flat illustration>"),
        AUTO("<auto>"),
        ;

        private final String name;
        Style(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Size implements BaseEnum {

        RESOLUTION_1024_1024("1024*1024", 1024, 1024),
        RESOLUTION_720_1280("720*1280", 720, 1280),
        RESOLUTION_1280_720("1280*720", 1280, 720),
        ;

        private final String name;
        private final Integer width;
        private final Integer height;
        Size(String name, Integer width, Integer height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        @Override
        public String getName() {
            return name;
        }

        public Integer getWidth() {
            return width;
        }

        public Integer getHeight() {
            return height;
        }
    }

    public enum RefMode implements BaseEnum {

        REFONLY("refonly"),
        REPAINT("repaint"),
        ;

        private final String name;
        RefMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }

}
