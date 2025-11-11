package com.luohuo.flex.ai.controller.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - AI 视频生成 Response VO")
@Data
public class AiVideoRespVO implements Serializable {

	@Schema(description = "编号", example = "1024")
	private Long id;

	@Schema(description = "用户编号", example = "1")
	private Long userId;

	@Schema(description = "提示词", example = "生成一段海边日落的视频")
	private String prompt;

	@Schema(description = "平台", example = "SiliconFlow")
	private String platform;

	@Schema(description = "模型编号", example = "1024")
	private Long modelId;

	@Schema(description = "模型标识", example = "Pro/text-to-video")
	private String model;

	@Schema(description = "视频宽度", example = "1280")
	private Integer width;

	@Schema(description = "视频高度", example = "720")
	private Integer height;

	@Schema(description = "视频时长（秒）", example = "5")
	private Integer duration;

	@Schema(description = "生成状态", example = "20")
	private Integer status;

	@Schema(description = "完成时间")
	private LocalDateTime finishTime;

	@Schema(description = "错误信息", example = "生成失败")
	private String errorMessage;

	@Schema(description = "视频地址", example = "https://example.com/video.mp4")
	private String videoUrl;

	@Schema(description = "视频封面图片地址", example = "https://example.com/cover.jpg")
	private String coverUrl;

	@Schema(description = "是否公开", example = "false")
	private Boolean publicStatus;

	@Schema(description = "生成参数")
	private Map<String, Object> options;

	@Schema(description = "任务编号", example = "task-123")
	private String taskId;

	@Schema(description = "创建时间")
	private LocalDateTime createTime;

	@Schema(description = "更新时间")
	private LocalDateTime updateTime;
}