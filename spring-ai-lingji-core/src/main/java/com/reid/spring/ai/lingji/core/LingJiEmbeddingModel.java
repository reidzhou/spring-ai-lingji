package com.reid.spring.ai.lingji.core;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingUsage;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.*;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

import java.util.List;

public class LingJiEmbeddingModel extends AbstractEmbeddingModel {

    private final LingJiEmbeddingOptions defaultOptions = new LingJiEmbeddingOptions(TextEmbedding.Models.TEXT_EMBEDDING_V1);

    private final String apiKey;

    private final TextEmbedding textEmbedding;

    private final RetryTemplate retryTemplate;

    private final MetadataMode metadataMode;

    public LingJiEmbeddingModel(String apiKey, TextEmbedding textEmbedding) {
        this(apiKey, textEmbedding, MetadataMode.EMBED);
    }

    public LingJiEmbeddingModel(String apiKey, TextEmbedding textEmbedding, MetadataMode metadataMode) {
        this(apiKey, textEmbedding, metadataMode, RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    public LingJiEmbeddingModel(String apiKey,
                                TextEmbedding textEmbedding,
                                MetadataMode metadataMode,
                                RetryTemplate retryTemplate) {
        Assert.notNull(apiKey, "The apiKey can not be null.");
        Assert.notNull(textEmbedding, "The textEmbedding can not be null.");
        Assert.notNull(metadataMode, "The metadataMode can not be null.");
        Assert.notNull(retryTemplate, "The retryTemplate can not be null.");

        this.apiKey = apiKey;
        this.textEmbedding = textEmbedding;
        this.metadataMode = metadataMode;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        EmbeddingResponse response = this.retryTemplate.execute(ctx -> {

            try {
                return LingJiEmbeddingModel.this.doCallWithRetryStrategy(request);
            } catch (NoApiKeyException e) {
                throw new RuntimeException(e);
            }

        });

        return response == null ? new EmbeddingResponse(List.of()) : response;
    }

    private EmbeddingResponse doCallWithRetryStrategy(EmbeddingRequest request) throws NoApiKeyException {
        LingJiEmbeddingOptions combinedOptions = ModelOptionsUtils.merge(
                request.getOptions(),
                this.defaultOptions,
                LingJiEmbeddingOptions.class
        );

        TextEmbeddingParam param = TextEmbeddingParam
                .builder()
                .model(combinedOptions.getModel())
                .apiKey(this.apiKey)
                .texts(request.getInstructions())
                .build();

        TextEmbeddingResult result = this.textEmbedding.call(param);

        result.getOutput();

        List<Embedding> embeddings = result.getOutput().getEmbeddings()
                .stream()
                .map(e -> new Embedding(e.getEmbedding(), e.getTextIndex()))
                .toList();

        TextEmbeddingUsage usage = result.getUsage();
        EmbeddingResponseMetadata metadata = new EmbeddingResponseMetadata();
        metadata.put("model", combinedOptions.getModel());
        metadata.put("request-id", result.getRequestId());
        metadata.put("total-tokens", usage.getTotalTokens());

        return new EmbeddingResponse(embeddings, metadata);
    }

    @Override
    public List<Double> embed(Document document) {
        Assert.notNull(document, "The document must not be null");
        return this.embed(document.getFormattedContent(this.metadataMode));
    }
}
