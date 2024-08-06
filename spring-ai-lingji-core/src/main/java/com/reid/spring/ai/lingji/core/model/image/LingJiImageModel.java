package com.reid.spring.ai.lingji.core.model.image;

import com.reid.spring.ai.lingji.core.exception.LingJiException;
import com.reid.spring.ai.lingji.core.exception.LingJiRPCException;
import com.reid.spring.ai.lingji.core.utils.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.*;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class LingJiImageModel implements ImageModel {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String apiKey;

    private final RestClient restClient;

    private final LingJiImageOptions options;

    public LingJiImageModel(String apiKey) {
        this(apiKey, RestClient.builder().build());
    }

    public LingJiImageModel(String apiKey, RestClient restClient) {
        this(apiKey, restClient, Constants.DEFAULT_OPTIONS);
    }

    public LingJiImageModel(String apiKey, RestClient restClient, LingJiImageOptions options) {
        this(apiKey, restClient, options, null);
    }

    public LingJiImageModel(String apiKey,
                            RestClient restClient,
                            LingJiImageOptions options,
                            ScheduledThreadPoolExecutor taskSyncExecutor) {
        Assert.notNull(apiKey, "The apiKey can not be null.");
        Assert.notNull(restClient, "The restClient can not be null.");
        Assert.notNull(options, "The options can not be null.");

        this.apiKey = apiKey;
        this.restClient = restClient;
        this.options = options;
    }

    @Override
    public ImageResponse call(ImagePrompt request) {
        LingJiImageRequest lingJiImageRequest = convert2ImageRequest(request);

        LingJiImageResponse.ImageCreateResponse imageCreateResponse = this.createImageTask(lingJiImageRequest);

        String taskId = imageCreateResponse.getOutput().getTaskId();

        try {

            return this.waitForResult(taskId);
        } catch (TimeoutException e) {
            this.logger.error("LingJi image calling timeout", e);
        } catch (InterruptedException e) {
            this.logger.error("LingJi image calling error", e);
        }

        return new ImageResponse(Collections.emptyList());
    }

    private LingJiImageRequest convert2ImageRequest(ImagePrompt request) {
        LingJiImageRequest lingJiImageRequest = new LingJiImageRequest();

        LingJiImageRequest.Input input = new LingJiImageRequest.Input();
        List<ImageMessage> imageMessages = request.getInstructions();
        if (imageMessages != null && !imageMessages.isEmpty()) {
            input.setPrompt(imageMessages.get(0).getText());
        }
        lingJiImageRequest.setInput(input);

        ImageOptions imageOptions = request.getOptions();
        LingJiImageOptions combinedOptions = ModelOptionsUtils.merge(imageOptions, this.options, LingJiImageOptions.class);

        LingJiImageRequest.Parameters parameters = new LingJiImageRequest.Parameters();
        parameters.setStyle(combinedOptions.getStyle());
        parameters.setSize(combinedOptions.getSize());
        parameters.setSeed(combinedOptions.getSeed());
        parameters.setN(combinedOptions.getN());
        parameters.setRefStrength(combinedOptions.getRefStrength());
        parameters.setRefMode(combinedOptions.getRefMode());

        lingJiImageRequest.setParameters(parameters);

        String model = imageOptions.getModel();
        if (!StringUtils.hasText(model)) {
            model = this.options.getModel();
        }
        lingJiImageRequest.setModel(model);

        this.checkRequest(lingJiImageRequest);
        return lingJiImageRequest;
    }

    private void checkRequest(LingJiImageRequest request) {
        String model = request.getModel();
        if (Model.WANX_V1.getName().equals(model)) {
            throw new LingJiException("unknown image model [" + model + "]");
        }

        LingJiImageRequest.Input input = request.getInput();
        if (input == null) {
            throw new LingJiException("request input should not be null");
        }

        String prompt = input.getPrompt();
        if (!StringUtils.hasText(prompt)) {
            throw new LingJiException("request prompt should not be null");
        }

        if (prompt.length() > 500) {
            logger.warn("prompt is too long [ size = " + prompt.length() + " maxSize = 500 ], would be split automatically");
        }

        String negativePrompt = input.getNegativePrompt();
        if (StringUtils.hasText(negativePrompt) && negativePrompt.length() > 500) {
            logger.warn("negative_prompt is too long [ size = " + negativePrompt.length() + " maxSize = 500 ], would be split automatically");
        }

        LingJiImageRequest.Parameters parameters = request.getParameters();
        if (parameters == null) {
            throw new LingJiException("request parameters should not be null");
        }

        String style = parameters.getStyle();
        EnumUtils.getEnumByNameOrElseThrow(Constants.Style.class, style, "unknown image style [" + style + "]");

        String size = parameters.getSize();
        EnumUtils.getEnumByNameOrElseThrow(Constants.Size.class, size, "unknown image size [" + size + "]");

        Integer n = parameters.getN();
        if (n != null && (n <= 0 || n > 4)) {
            throw new LingJiException("range of n should be [1, 4]");
        }

        Long seed = parameters.getSeed();
        if (seed != null && (seed <= 0 || seed >= 4294967290L)) {
            throw new LingJiException("range of seed should be (0, 4294967290)");
        }

        Float refStrength = parameters.getRefStrength();
        if (refStrength != null && (refStrength < 0 || refStrength > 1)) {
            throw new LingJiException("range of ref_strength should be [0.0, 1.0]");
        }

        String refMode = parameters.getRefMode();
        EnumUtils.getEnumByNameOrElseThrow(Constants.RefMode.class, refMode, "unknown ref mode [" + refMode + "]");

    }

    private LingJiImageResponse.ImageCreateResponse createImageTask(LingJiImageRequest lingJiImageRequest) {
        ResponseEntity<LingJiImageResponse.ImageCreateResponse> responseEntity = this.restClient.post()
                .uri(Constants.BASE_URL + Constants.TASK_CREATE_URI)
                .headers((headers) -> {
                    headers.add("X-DashScope-Async", "enable");
                    headers.setBearerAuth(this.apiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(lingJiImageRequest)
                .retrieve()
                .toEntity(LingJiImageResponse.ImageCreateResponse.class);

        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        LingJiImageResponse.ImageCreateResponse imageCreateResponse = responseEntity.getBody();

        if (httpStatusCode.is2xxSuccessful()) {
            this.logger.debug(
                    "Create LingJi image task success [ response = {} ]",
                    ModelOptionsUtils.toJsonString(imageCreateResponse));

            return imageCreateResponse;
        } else {

            if (imageCreateResponse == null) {
                this.logger.debug(
                        "Create LingJi image task failed [ http_code = {} ]",
                        httpStatusCode.value());

                throw new LingJiRPCException(httpStatusCode.value());
            } else {

                this.logger.debug(
                        "Create LingJi image task failed [ http_code = {}, response = {} ]",
                        httpStatusCode.value(), ModelOptionsUtils.toJsonString(imageCreateResponse));

                throw new LingJiRPCException(
                        httpStatusCode.value(),
                        imageCreateResponse.getRequestId(),
                        imageCreateResponse.getCode(),
                        imageCreateResponse.getMessage()
                );
            }
        }

    }

    private ImageResponse convert2ImageResponse(LingJiImageResponse.ImageTaskResponse response) {

        List<LingJiImageResponse.ImageTaskResponse.Output.Result> results = response.getOutput().getResults();
        List<ImageGeneration> imageGenerations = results
                .stream()
                .map(result -> new ImageGeneration(new Image(result.getUrl(), null)))
                .collect(Collectors.toList());


        LingJiImageResponse.ImageTaskResponse.TaskMetrics taskMetrics = response.getTaskMetrics();
        ImageResponseMetadata metadata = new ImageResponseMetadata.DefaultImageResponseMetadata();
        metadata.put("image_count", response.getUsage().getImageCount());
        metadata.put("total", taskMetrics.getTotal());
        metadata.put("success", taskMetrics.getSucceeded());
        metadata.put("fail", taskMetrics.getFailed());

        return new ImageResponse(imageGenerations, metadata);
    }

    private ImageResponse waitForResult(String taskId) throws InterruptedException, TimeoutException {
        Integer timeout = this.options.getSyncTimeout();
        Integer interval = this.options.getSyncInterval();

        int scrollTimes = 0;

        while (true) {

            LingJiImageResponse.ImageTaskResponse imageTaskResponse = this.fetchTaskResult(taskId);

            if (StringUtils.hasText(imageTaskResponse.getMessage())) {
                return new ImageResponse(Collections.emptyList());
            }

            LingJiImageResponse.TaskStatus taskStatus = imageTaskResponse.getOutput().getTaskStatus();
            if (LingJiImageResponse.TaskStatus.UNKNOWN.equals(taskStatus)
                    || LingJiImageResponse.TaskStatus.FAILED.equals(taskStatus)) {
                return new ImageResponse(Collections.emptyList());
            } else if (LingJiImageResponse.TaskStatus.SUCCEEDED.equals(taskStatus)) {

                return LingJiImageModel.this.convert2ImageResponse(imageTaskResponse);

            }

            scrollTimes++;
            if (scrollTimes * interval >= timeout) {
                throw new TimeoutException();
            }

            Thread.sleep(interval * 1000);
        }
    }

    private LingJiImageResponse.ImageTaskResponse fetchTaskResult(String taskId) {
        ResponseEntity<LingJiImageResponse.ImageTaskResponse> responseEntity = LingJiImageModel.this.restClient
                .post()
                .uri(String.format(Constants.BASE_URL + Constants.TASK_FETCH_URI, taskId))
                .headers((headers) -> {
                    headers.setBearerAuth(LingJiImageModel.this.apiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve()
                .toEntity(LingJiImageResponse.ImageTaskResponse.class);

        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        LingJiImageResponse.ImageTaskResponse imageTaskResponse = responseEntity.getBody();

        if (httpStatusCode.is2xxSuccessful()) {
            this.logger.debug(
                    "Fetch LingJi image task result success [ response = {} ]",
                    ModelOptionsUtils.toJsonString(imageTaskResponse));
        } else {

            if (imageTaskResponse == null) {
                this.logger.debug(
                        "Fetch LingJi image task result failed [ http_code = {} ]",
                        httpStatusCode.value());

                imageTaskResponse = new LingJiImageResponse.ImageTaskResponse();
                imageTaskResponse.setCode("500");
                imageTaskResponse.setMessage("http error [code = " + httpStatusCode.value() + "]");

            } else {
                this.logger.debug(
                        "Fetch LingJi image task result failed [ http_code = {}, response = {} ]",
                        httpStatusCode.value(), ModelOptionsUtils.toJsonString(imageTaskResponse));
            }
        }

        return imageTaskResponse;
    }

}
