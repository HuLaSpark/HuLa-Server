package com.luohuo.flex.ai.core.model.silicon;

import com.luohuo.flex.ai.core.model.strategy.SiliconFlowVideoStatusStrategy;
import com.luohuo.flex.ai.core.model.strategy.VideoStatusStrategy;
import com.luohuo.flex.ai.core.model.video.VideoModel;
import com.luohuo.flex.ai.core.model.video.VideoOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 硅基流动视频生成模型实现
 *
 * @author 乾乾
 */
@Slf4j
public class SiliconFlowVideoModel implements VideoModel {

    private final SiliconFlowVideoApi siliconFlowVideoApi;
    private final SiliconFlowVideoOptions defaultOptions;
    private final VideoStatusStrategy statusStrategy;

    public SiliconFlowVideoModel(SiliconFlowVideoApi siliconFlowVideoApi) {
        this(siliconFlowVideoApi, SiliconFlowVideoOptions.builder().build());
    }

    public SiliconFlowVideoModel(SiliconFlowVideoApi siliconFlowVideoApi, SiliconFlowVideoOptions options) {
        Assert.notNull(siliconFlowVideoApi, "SiliconFlowVideoApi must not be null");
        Assert.notNull(options, "options must not be null");
        this.siliconFlowVideoApi = siliconFlowVideoApi;
        this.defaultOptions = options;
        this.statusStrategy = new SiliconFlowVideoStatusStrategy();
    }

    @Override
    public String submitVideo(String prompt, VideoOptions options) {
        Assert.hasLength(prompt, "Prompt cannot be empty");

        // 转换为硅基流动的参数格式
        SiliconFlowVideoOptions siliconOptions = convertToSiliconFlowOptions(options);

        // 合并默认参数和运行时参数
        SiliconFlowVideoOptions mergedOptions = mergeOptions(siliconOptions);

        SiliconFlowVideoApi.SiliconflowVideoRequest request = new SiliconFlowVideoApi.SiliconflowVideoRequest(
                prompt,
                mergedOptions.getModel(),
                mergedOptions.getImageSize(),
                mergedOptions.getNegativePrompt(),
                mergedOptions.getImage(),
                mergedOptions.getSeed()
        );

        ResponseEntity<SiliconFlowVideoApi.SiliconflowVideoSubmitResponse> response = siliconFlowVideoApi.submitVideo(request);
        if (response.getBody() == null) {
            throw new RuntimeException("提交视频生成任务失败，响应为空");
        }

        return response.getBody().requestId();
    }

    /**
     * 获取视频生成状态
     *
     * @param requestId 请求ID
     * @return 视频生成状态响应
     */
    @Override
    public VideoStatusResponse getVideoStatus(String requestId) {
        Assert.hasLength(requestId, "Request ID cannot be empty");

        ResponseEntity<SiliconFlowVideoApi.SiliconflowVideoStatusResponse> response =
                siliconFlowVideoApi.getVideoStatus(requestId);

        if (response.getBody() == null) {
            throw new RuntimeException("获取视频状态失败，响应为空");
        }

        SiliconFlowVideoApi.SiliconflowVideoStatusResponse apiResponse = response.getBody();

        // 将API响应转换为标准接口响应
        return new VideoStatusResponse() {
            @Override
            public String getStatus() {
                return apiResponse.status();
            }

            @Override
            public List<String> getVideoUrls() {
                if (apiResponse.videos() == null || apiResponse.videos().isEmpty()) {
                    return null;
                }
                return apiResponse.videos().stream()
                        .map(SiliconFlowVideoApi.VideoResult::url)
                        .collect(Collectors.toList());
            }

            @Override
            public String getMessage() {
                return apiResponse.message();
            }
        };
    }

    /**
     * 获取硅基流动的视频生成状态（内部使用）
     */
    private SiliconFlowVideoApi.SiliconflowVideoStatusResponse getSiliconFlowVideoStatus(String requestId) {
        Assert.hasLength(requestId, "Request ID cannot be empty");

        ResponseEntity<SiliconFlowVideoApi.SiliconflowVideoStatusResponse> response =
                siliconFlowVideoApi.getVideoStatus(requestId);

        if (response.getBody() == null) {
            throw new RuntimeException("获取视频状态失败，响应为空");
        }

        return response.getBody();
    }

    @Override
    public String pollVideoResult(String requestId, int maxRetries, long retryIntervalMs) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                SiliconFlowVideoApi.SiliconflowVideoStatusResponse statusResponse = getSiliconFlowVideoStatus(requestId);
                String status = statusResponse.status();

                if (statusStrategy.isSuccessStatus(status)) {
                    // 成功状态
                    if (statusResponse.videos() != null && !statusResponse.videos().isEmpty()) {
                        String videoUrl = statusResponse.videos().get(0).url();
                        log.info("[pollVideoResult][requestId({}) 视频生成成功] videoUrl={}", requestId, videoUrl);
                        return videoUrl;
                    } else {
                        log.info("[pollVideoResult][requestId({}) 状态为成功但视频列表为空，继续等待视频生成完成]", requestId);
                    }
                } else if (statusStrategy.isFailureStatus(status)) {
                    // 失败状态
                    String errorMsg = statusResponse.message() != null ? statusResponse.message() : "视频生成失败";
                    throw new RuntimeException("视频生成失败: " + errorMsg);
                } else if (statusStrategy.isProcessingStatus(status)) {
                    // 处理中状态
                    log.info("[pollVideoResult][requestId({}) 视频生成中，第 {} 次轮询]", requestId, i + 1);
                } else {
                    // 未知状态
                    log.warn("[pollVideoResult][requestId({}) 未知状态: {}]", requestId, status);
                }

                if (i < maxRetries - 1) {
                    Thread.sleep(retryIntervalMs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("轮询被中断", e);
            }
        }

        throw new RuntimeException("视频生成超时，已重试 " + maxRetries + " 次");
    }

    /**
     * 将通用的 VideoOptions 转换为硅基流动的参数格式
     */
    private SiliconFlowVideoOptions convertToSiliconFlowOptions(VideoOptions options) {
        if (options instanceof SiliconFlowVideoOptions) {
            return (SiliconFlowVideoOptions) options;
        }

        return SiliconFlowVideoOptions.builder()
                .model(options.getModel())
                .imageSize(options.getImageSize())
                .width(options.getWidth())
                .height(options.getHeight())
                .duration(options.getDuration())
                .negativePrompt(options.getNegativePrompt())
                .seed(options.getSeed())
                .image(options.getImage())
                .build();
    }

    /**
     * 合并默认参数和运行时参数
     */
    private SiliconFlowVideoOptions mergeOptions(SiliconFlowVideoOptions runtimeOptions) {
        if (runtimeOptions == null) {
            return defaultOptions;
        }

        return SiliconFlowVideoOptions.builder()
                .model(runtimeOptions.getModel() != null ? runtimeOptions.getModel() : defaultOptions.getModel())
                .imageSize(runtimeOptions.getImageSize() != null ? runtimeOptions.getImageSize() : defaultOptions.getImageSize())
                .negativePrompt(runtimeOptions.getNegativePrompt() != null ? runtimeOptions.getNegativePrompt() : defaultOptions.getNegativePrompt())
                .image(runtimeOptions.getImage() != null ? runtimeOptions.getImage() : defaultOptions.getImage())
                .seed(runtimeOptions.getSeed() != null ? runtimeOptions.getSeed() : defaultOptions.getSeed())
                .width(runtimeOptions.getWidth() != null ? runtimeOptions.getWidth() : defaultOptions.getWidth())
                .height(runtimeOptions.getHeight() != null ? runtimeOptions.getHeight() : defaultOptions.getHeight())
                .duration(runtimeOptions.getDuration() != null ? runtimeOptions.getDuration() : defaultOptions.getDuration())
                .build();
    }
}