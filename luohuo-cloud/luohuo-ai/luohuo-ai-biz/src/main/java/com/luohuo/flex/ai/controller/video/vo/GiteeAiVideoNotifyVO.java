package com.luohuo.flex.ai.controller.video.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Gitee AI 视频生成回调通知 VO
 *
 * 根据官方文档：https://ai.gitee.com/docs/openapi/v1#tag/%E8%A7%86%E9%A2%91%E7%94%9F%E6%88%90
 *
 * @author 乾乾
 */
@Schema(description = "Gitee AI 视频生成回调通知")
@Data
public class GiteeAiVideoNotifyVO {

	@Schema(description = "任务唯一标识", required = true, example = "abc123")
	@JsonProperty("task_id")
	private String taskId;

	@Schema(description = "任务状态：waiting / in_progress / success / failure / cancelled",
			required = true,
			allowableValues = {"waiting", "in_progress", "success", "failure", "cancelled"},
			example = "success")
	@JsonProperty("status")
	private String status;

	@Schema(description = "输出结果（仅在 status=success 时出现，包含视频链接；链接 7 天有效，请及时下载）")
	@JsonProperty("output")
	private VideoOutput output;

	@Schema(description = "任务创建时间", required = true, example = "2024-01-01T12:00:00")
	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@Schema(description = "任务开始处理时间（未开始时为空）", example = "2024-01-01T12:00:05")
	@JsonProperty("started_at")
	private LocalDateTime startedAt;

	@Schema(description = "任务完成时间（未完成时为空）", example = "2024-01-01T12:05:00")
	@JsonProperty("completed_at")
	private LocalDateTime completedAt;

	@Schema(description = "任务相关链接", required = true)
	@JsonProperty("urls")
	private TaskUrls urls;

	/**
	 * 视频输出结果
	 * 注意：链接仅 7 天有效，请及时下载保存
	 */
	@Data
	public static class VideoOutput {
		@Schema(description = "视频URL（7天有效）", example = "https://example.com/video.mp4")
		@JsonProperty("video_url")
		private String videoUrl;
	}

	/**
	 * 任务相关链接
	 */
	@Data
	public static class TaskUrls {
		@Schema(description = "查询任务链接", example = "https://ai.gitee.com/v1/async/videos/generations/abc123")
		@JsonProperty("get")
		private String get;

		@Schema(description = "取消任务链接", example = "https://ai.gitee.com/v1/async/videos/generations/abc123/cancel")
		@JsonProperty("cancel")
		private String cancel;
	}
}
