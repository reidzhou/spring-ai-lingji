package com.reid.spring.ai.lingji.core.model.embeddings;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LingJiEmbeddingModelTest {

    @Resource
    LingJiEmbeddingModel lingJiEmbeddingModel;

    @Test
    void defaultEmbedding() {
        assertThat(lingJiEmbeddingModel.dimensions()).isNotNull();
        assertThat(lingJiEmbeddingModel.dimensions()).isEqualTo(1536);
    }

    @Test
    void singleSentenceEmbedding() {
        List<String> inputs = new ArrayList<>() {{
           add("lingji embeddings test");
        }};

        EmbeddingRequest embeddingRequest = new EmbeddingRequest(inputs, EmbeddingOptions.EMPTY);

        EmbeddingResponse embeddingResponse = lingJiEmbeddingModel.call(embeddingRequest);

        assertThat(embeddingResponse.getResults().size()).isEqualTo(1);
        assertThat(embeddingResponse.getResults().get(0).getIndex()).isEqualTo(0);
        assertThat(embeddingResponse.getResults().get(0).getOutput().size()).isEqualTo(1536);
        assertThat(embeddingResponse.getMetadata().get("total_tokens")).isEqualTo(5);

    }

    @Test
    void multiSentenceEmbedding() {
        List<String> inputs = new ArrayList<>() {{
            add("lingji embeddings test");
            add("lingji embeddings test");
        }};

        EmbeddingRequest embeddingRequest = new EmbeddingRequest(inputs, EmbeddingOptions.EMPTY);

        EmbeddingResponse embeddingResponse = lingJiEmbeddingModel.call(embeddingRequest);

        assertThat(embeddingResponse.getResults().size()).isEqualTo(2);
        assertThat(embeddingResponse.getResults().get(0).getIndex()).isEqualTo(0);
        assertThat(embeddingResponse.getResults().get(0).getOutput().size()).isEqualTo(1536);
        assertThat(embeddingResponse.getMetadata().get("total_tokens")).isEqualTo(10);

    }

}