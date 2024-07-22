package com.reid.spring.ai.lingji.core.model.embeddings;

public class Constants {

    public final static String LING_JI_EMBEDDINGS_V1 = "text-embedding-v1";

    public final static String LING_JI_EMBEDDINGS_V2 = "text-embedding-v2";

    public final static LingJiEmbeddingOptions DEFAULT_OPTIONS = LingJiEmbeddingOptions
            .builder().withModel(LING_JI_EMBEDDINGS_V1).build();

    public final static String LING_JI_EMBEDDINGS_URL = "https://dashscope.aliyuncs.com/api/v1/services/embeddings/text-embedding/text-embedding";

}
