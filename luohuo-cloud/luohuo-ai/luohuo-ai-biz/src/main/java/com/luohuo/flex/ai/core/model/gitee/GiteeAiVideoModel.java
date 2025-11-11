package com.luohuo.flex.ai.core.model.gitee;

import com.luohuo.flex.ai.core.model.strategy.GiteeAiVideoStatusStrategy;
import com.luohuo.flex.ai.core.model.strategy.VideoStatusStrategy;
import com.luohuo.flex.ai.core.model.video.VideoModel;
import com.luohuo.flex.ai.core.model.video.VideoOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * Gitee AI 视频生成模型实现类
 *
 * <p>实现 {@link VideoModel} 接口，封装 Gitee AI 平台的视频生成功能。
 * 支持文本生成视频(T2V)等视频生成能力。</p>
 *
 * @author 乾乾
 * @see VideoModel
 * @see GiteeAiVideoOptions
 * @see GiteeAiVideoApi
 */
@Slf4j
public class GiteeAiVideoModel implements VideoModel {

	private final GiteeAiVideoApi giteeAiVideoApi;
	private final GiteeAiVideoOptions defaultOptions;
	private final VideoStatusStrategy statusStrategy;

	public GiteeAiVideoModel(GiteeAiVideoApi giteeAiVideoApi) {
		this(giteeAiVideoApi, GiteeAiVideoOptions.builder().build());
	}

	public GiteeAiVideoModel(GiteeAiVideoApi giteeAiVideoApi, GiteeAiVideoOptions options) {
		Assert.notNull(giteeAiVideoApi, "GiteeAiVideoApi must not be null");
		Assert.notNull(options, "options must not be null");
		this.giteeAiVideoApi = giteeAiVideoApi;
		this.defaultOptions = options;
		this.statusStrategy = new GiteeAiVideoStatusStrategy();
	}

	@Override
	public String submitVideo(String prompt, VideoOptions options) {
		Assert.hasLength(prompt, "Prompt cannot be empty");

		// 转换为 Gitee AI 的参数格式
		GiteeAiVideoOptions giteeOptions = convertToGiteeAiOptions(options);

		// 合并默认参数和运行时参数
		GiteeAiVideoOptions mergedOptions = mergeOptions(giteeOptions);

		// 构建请求对象
		GiteeAiVideoApi.GiteeAiVideoRequest request = new GiteeAiVideoApi.GiteeAiVideoRequest(
				mergedOptions.getModel(),
				prompt
		);

		// 提交视频生成任务
		ResponseEntity<GiteeAiVideoApi.GiteeAiVideoSubmitResponse> response = giteeAiVideoApi.submitVideo(request);
		if (response.getBody() == null) {
			throw new RuntimeException("提交视频生成任务失败，响应为空");
		}

		String taskId = response.getBody().taskId();
		log.info("[submitVideo][Gitee AI 视频生成任务提交成功] taskId={}, model={}, status={}",
				taskId, mergedOptions.getModel(), response.getBody().status());
		return taskId;
	}

	@Override
	public VideoStatusResponse getVideoStatus(String taskId) {
		Assert.hasLength(taskId, "Task ID cannot be empty");

		log.info("[getVideoStatus][Gitee AI 查询视频状态] taskId={}", taskId);
		ResponseEntity<GiteeAiVideoApi.GiteeAiVideoStatusResponse> response = giteeAiVideoApi.getVideoStatus(taskId);

		if (response.getBody() == null) {
			throw new RuntimeException("获取视频状态失败，响应为空");
		}

		GiteeAiVideoApi.GiteeAiVideoStatusResponse apiResponse = response.getBody();

		// Gitee AI 查询接口返回格式：{"file_url": "https://..."}
		// 如果有 file_url，说明视频已生成成功
		if (apiResponse.fileUrl() != null) {
			log.info("[getVideoStatus][Gitee AI 查询视频状态成功] taskId={}, fileUrl={}", taskId, apiResponse.fileUrl());
			return new VideoStatusResponse() {
				@Override
				public String getStatus() {
					return "success";  // 有 file_url 说明已成功
				}

				@Override
				public List<String> getVideoUrls() {
					return List.of(apiResponse.fileUrl());
				}

				@Override
				public String getMessage() {
					return null;
				}
			};
		}

		// 如果没有 file_url，说明还在处理中或失败了
		// 这种情况下，返回处理中状态，继续轮询
		log.info("[getVideoStatus][Gitee AI 视频还在处理中] taskId={}", taskId);
		return new VideoStatusResponse() {
			@Override
			public String getStatus() {
				return "in_progress";  // 继续轮询
			}

			@Override
			public List<String> getVideoUrls() {
				return Collections.emptyList();
			}

			@Override
			public String getMessage() {
				return null;
			}
		};
	}

	@Override
	public String pollVideoResult(String taskId, int maxRetries, long retryIntervalMs) {
		for (int i = 0; i < maxRetries; i++) {
			try {
				VideoStatusResponse statusResponse = getVideoStatus(taskId);
				String status = statusResponse.getStatus();

				if (statusStrategy.isSuccessStatus(status)) {
					// 成功状态
					List<String> videoUrls = statusResponse.getVideoUrls();
					if (videoUrls != null && !videoUrls.isEmpty()) {
						String videoUrl = videoUrls.get(0);
						log.info("[pollVideoResult][Gitee AI 视频生成成功] taskId={}, videoUrl={}", taskId, videoUrl);
						return videoUrl;
					} else {
						log.info("[pollVideoResult][taskId({}) 状态为成功但视频URL为空，继续等待]", taskId);
					}
				} else if (statusStrategy.isFailureStatus(status)) {
					// 失败状态
					String errorMsg = statusResponse.getMessage() != null ?
							statusResponse.getMessage() : "视频生成失败";
					log.error("[pollVideoResult][Gitee AI 视频生成失败] taskId={}, error={}", taskId, errorMsg);
					throw new RuntimeException("视频生成失败: " + errorMsg);
				} else if (statusStrategy.isProcessingStatus(status)) {
					// 处理中状态
					log.info("[pollVideoResult][Gitee AI 视频生成中] taskId={}, 第 {} 次轮询, status={}",
							taskId, i + 1, status);
				} else {
					// 未知状态
					log.warn("[pollVideoResult][taskId({}) 未知状态: {}]", taskId, status);
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
	 * 将通用的 VideoOptions 转换为 Gitee AI 的参数格式
	 */
	private GiteeAiVideoOptions convertToGiteeAiOptions(VideoOptions options) {
		if (options instanceof GiteeAiVideoOptions) {
			return (GiteeAiVideoOptions) options;
		}

		return GiteeAiVideoOptions.builder()
				.model(options.getModel())
				.width(options.getWidth())
				.height(options.getHeight())
				.duration(options.getDuration())
				.imageSize(options.getImageSize())
				.negativePrompt(options.getNegativePrompt())
				.seed(options.getSeed())
				.image(options.getImage())
				.build();
	}

	/**
	 * 合并默认参数和运行时参数
	 */
	private GiteeAiVideoOptions mergeOptions(GiteeAiVideoOptions runtimeOptions) {
		if (runtimeOptions == null) {
			return defaultOptions;
		}

		return GiteeAiVideoOptions.builder()
				.model(runtimeOptions.getModel() != null ? runtimeOptions.getModel() : defaultOptions.getModel())
				.width(runtimeOptions.getWidth() != null ? runtimeOptions.getWidth() : defaultOptions.getWidth())
				.height(runtimeOptions.getHeight() != null ? runtimeOptions.getHeight() : defaultOptions.getHeight())
				.duration(runtimeOptions.getDuration() != null ? runtimeOptions.getDuration() : defaultOptions.getDuration())
				.imageSize(runtimeOptions.getImageSize() != null ? runtimeOptions.getImageSize() : defaultOptions.getImageSize())
				.negativePrompt(runtimeOptions.getNegativePrompt() != null ? runtimeOptions.getNegativePrompt() : defaultOptions.getNegativePrompt())
				.seed(runtimeOptions.getSeed() != null ? runtimeOptions.getSeed() : defaultOptions.getSeed())
				.image(runtimeOptions.getImage() != null ? runtimeOptions.getImage() : defaultOptions.getImage())
				.build();
	}
}
