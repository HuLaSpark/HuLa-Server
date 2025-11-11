package com.luohuo.flex.ai.core.model.video;

public interface VideoModel {

    String submitVideo(String prompt, VideoOptions options);

    String pollVideoResult(String requestId, int maxRetries, long retryIntervalMs);

    VideoStatusResponse getVideoStatus(String requestId);

    interface VideoStatusResponse {
        String getStatus();

        java.util.List<String> getVideoUrls();

        String getMessage();
    }
}
