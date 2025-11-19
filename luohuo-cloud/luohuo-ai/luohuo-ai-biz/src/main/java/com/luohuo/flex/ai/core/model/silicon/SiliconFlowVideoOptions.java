package com.luohuo.flex.ai.core.model.silicon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luohuo.flex.ai.core.model.video.VideoOptions;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiliconFlowVideoOptions implements VideoOptions {

    /**
     * 模型名称
     */
    @JsonProperty("model")
    private String model;

    /**
     * 提示词
     */
    @JsonProperty("prompt")
    private String prompt;

    /**
     * 视频尺寸 (1280x720, 720x1280, 960x960)
     */
    @JsonProperty("image_size")
    private String imageSize;

    /**
     * 负面提示词
     */
    @JsonProperty("negative_prompt")
    private String negativePrompt;

    /**
     * 图片URL或base64 (用于i2v)
     */
    @JsonProperty("image")
    private String image;

    /**
     * 随机种子
     */
    @JsonProperty("seed")
    private Integer seed;

    /**
     * 视频宽度
     */
    private Integer width;

    /**
     * 视频高度
     */
    private Integer height;

    /**
     * 视频时长(秒)
     */
    private Integer duration;

    public static SiliconFlowVideoOptionsBuilder builder() {
        return new SiliconFlowVideoOptionsBuilder();
    }
}