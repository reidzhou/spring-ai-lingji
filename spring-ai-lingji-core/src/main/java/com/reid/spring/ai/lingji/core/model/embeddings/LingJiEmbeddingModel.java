package com.reid.spring.ai.lingji.core.model.embeddings;

import com.reid.spring.ai.lingji.core.exception.LingJiRPCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.*;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.List;

public class LingJiEmbeddingModel extends AbstractEmbeddingModel {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String apiKey;

    private final RestClient restClient;

    private final LingJiEmbeddingOptions options;

    private final MetadataMode metadataMode;

    public LingJiEmbeddingModel(String apiKey) {
        this(apiKey, RestClient.builder().build());
    }

    public LingJiEmbeddingModel(String apiKey, RestClient restClient) {
        this(apiKey, restClient, MetadataMode.EMBED);
    }

    public LingJiEmbeddingModel(String apiKey, RestClient restClient, MetadataMode metadataMode) {
        this(apiKey, restClient, metadataMode, Constants.DEFAULT_OPTIONS);
    }

    public LingJiEmbeddingModel(String apiKey,
                                RestClient restClient,
                                MetadataMode metadataMode,
                                LingJiEmbeddingOptions options) {
        Assert.notNull(apiKey, "The apiKey can not be null.");
        Assert.notNull(restClient, "The restClient can not be null.");
        Assert.notNull(metadataMode, "The metadataMode can not be null.");
        Assert.notNull(options, "The options can not be null.");

        this.apiKey = apiKey;
        this.restClient = restClient;
        this.metadataMode = metadataMode;
        this.options = options;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        return this.createResponse(doRpc(this.createRequest(request)));
    }

    @Override
    public List<Double> embed(Document document) {
        Assert.notNull(document, "The document must not be null");
        return this.embed(document.getFormattedContent(this.metadataMode));
    }

    private LingJiEmbeddingsRequest createRequest(EmbeddingRequest request) {
        EmbeddingOptions requestOptions = request.getOptions();
        LingJiEmbeddingsRequest lingJiEmbeddingsRequest = new LingJiEmbeddingsRequest();

        lingJiEmbeddingsRequest.setModel(this.options.getModel());
        lingJiEmbeddingsRequest.getInput().setTexts(request.getInstructions());

        if (requestOptions != null && !EmbeddingOptions.EMPTY.equals(requestOptions)) {
            lingJiEmbeddingsRequest = ModelOptionsUtils.merge(requestOptions, lingJiEmbeddingsRequest, LingJiEmbeddingsRequest.class);
        }

        return lingJiEmbeddingsRequest;
    }

    private EmbeddingResponse createResponse(LingJiEmbeddingsResponse lingJiEmbeddingsResponse) {

        EmbeddingResponseMetadata embeddingResponseMetadata = new EmbeddingResponseMetadata();
        embeddingResponseMetadata.put("total_tokens", lingJiEmbeddingsResponse.getUsage().getTotalTokens());

        List<Embedding> embeddings = lingJiEmbeddingsResponse
                .getOutput()
                .getEmbeddings()
                .stream()
                .map(embedding -> new Embedding(embedding.getEmbedding(), embedding.getTextIndex()))
                .toList();

        return new EmbeddingResponse(embeddings, embeddingResponseMetadata);
    }

    private LingJiEmbeddingsResponse doRpc(LingJiEmbeddingsRequest request) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(
                    "Request LingJi Embedding Model [ url = {}, request = {} ]",
                    Constants.LING_JI_EMBEDDINGS_URL, ModelOptionsUtils.toJsonString(request));
        }

        ResponseEntity<LingJiEmbeddingsResponse> responseEntity = this.restClient
                .post()
                .uri(Constants.LING_JI_EMBEDDINGS_URL)
                .headers((headers) -> {
                    headers.setBearerAuth(this.apiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(request)
                .retrieve()
                .toEntity(LingJiEmbeddingsResponse.class);

        LingJiEmbeddingsResponse lingJiEmbeddingsResponse = responseEntity.getBody();
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();

        if (httpStatusCode.is2xxSuccessful()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(
                        "Request LingJi Embedding Model Success [ response = {} ]",
                        ModelOptionsUtils.toJsonString(lingJiEmbeddingsResponse));
            }

            return lingJiEmbeddingsResponse;
        } else {

            if (lingJiEmbeddingsResponse == null) {
                this.logger.debug(
                        "Request LingJi Embedding Model failed [ http_code = {} ]",
                        httpStatusCode.value());

                throw new LingJiRPCException(httpStatusCode.value());
            } else {

                this.logger.debug(
                        "Request LingJi Embedding Model failed [ http_code = {}, err = {} ]",
                        httpStatusCode.value(), ModelOptionsUtils.toJsonString(lingJiEmbeddingsResponse));

                throw new LingJiRPCException(
                        httpStatusCode.value(),
                        lingJiEmbeddingsResponse.getRequestId(),
                        lingJiEmbeddingsResponse.getCode(),
                        lingJiEmbeddingsResponse.getMessage()
                );
            }
        }

    }

}
