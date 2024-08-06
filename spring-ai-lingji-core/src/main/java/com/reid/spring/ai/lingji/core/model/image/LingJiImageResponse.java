package com.reid.spring.ai.lingji.core.model.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reid.spring.ai.lingji.core.BaseEnum;
import com.reid.spring.ai.lingji.core.common.LingJiRPCCommonResponse;

import java.util.ArrayList;
import java.util.List;

public class LingJiImageResponse {

    public static class ImageCreateResponse extends LingJiRPCCommonResponse {

        @JsonProperty(value = "output")
        private Output output;

        public static class Output {

            @JsonProperty(value = "task_id")
            private String taskId;

            @JsonProperty(value = "task_status")
            private TaskStatus taskStatus;

            public String getTaskId() {
                return taskId;
            }

            public void setTaskId(String taskId) {
                this.taskId = taskId;
            }

            public TaskStatus getTaskStatus() {
                return taskStatus;
            }

            public void setTaskStatus(TaskStatus taskStatus) {
                this.taskStatus = taskStatus;
            }
        }

        public Output getOutput() {
            return output;
        }

        public void setOutput(Output output) {
            this.output = output;
        }
    }

    public static class ImageTaskResponse extends LingJiRPCCommonResponse {

        @JsonProperty(value = "output")
        private Output output;

        @JsonProperty(value = "task_metrics")
        private TaskMetrics taskMetrics;

        @JsonProperty(value = "usage")
        private Usage usage;

        public static class Output {

            @JsonProperty(value = "task_id")
            private String taskId;

            @JsonProperty(value = "task_status")
            private TaskStatus taskStatus;

            @JsonProperty(value = "results")
            private List<Result> results = new ArrayList<>();

            public String getTaskId() {
                return taskId;
            }

            public void setTaskId(String taskId) {
                this.taskId = taskId;
            }

            public TaskStatus getTaskStatus() {
                return taskStatus;
            }

            public void setTaskStatus(TaskStatus taskStatus) {
                this.taskStatus = taskStatus;
            }

            public List<Result> getResults() {
                return results;
            }

            public void setResults(List<Result> results) {
                this.results = results;
            }

            public static class Result {

                @JsonProperty(value = "url")
                private String url;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }

        }

        public static class TaskMetrics {

            @JsonProperty(value = "TOTAL")
            private Integer total;

            @JsonProperty(value = "SUCCEEDED")
            private Integer succeeded;

            @JsonProperty(value = "FAILED")
            private Integer failed;

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public Integer getSucceeded() {
                return succeeded;
            }

            public void setSucceeded(Integer succeeded) {
                this.succeeded = succeeded;
            }

            public Integer getFailed() {
                return failed;
            }

            public void setFailed(Integer failed) {
                this.failed = failed;
            }
        }

        public static class Usage {

            @JsonProperty(value = "image_count")
            private Integer imageCount;

            public Integer getImageCount() {
                return imageCount;
            }

            public void setImageCount(Integer imageCount) {
                this.imageCount = imageCount;
            }
        }

        public Output getOutput() {
            return output;
        }

        public void setOutput(Output output) {
            this.output = output;
        }

        public TaskMetrics getTaskMetrics() {
            return taskMetrics;
        }

        public void setTaskMetrics(TaskMetrics taskMetrics) {
            this.taskMetrics = taskMetrics;
        }

        public Usage getUsage() {
            return usage;
        }

        public void setUsage(Usage usage) {
            this.usage = usage;
        }
    }

    public enum TaskStatus implements BaseEnum {
        PENDING("PENDING"),
        RUNNING("RUNNING"),
        SUCCEEDED("SUCCEEDED"),
        FAILED("FAILED"),
        UNKNOWN("UNKNOWN")
        ;

        private final String name;
        TaskStatus(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }

}
