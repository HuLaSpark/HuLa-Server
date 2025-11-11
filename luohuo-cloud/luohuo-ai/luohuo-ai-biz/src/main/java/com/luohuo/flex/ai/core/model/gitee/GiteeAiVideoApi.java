package com.luohuo.flex.ai.core.model.gitee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Gitee AI Video API 客户端
 *
 * <p>封装 Gitee AI 平台的视频生成 API 调用，支持异步视频生成任务的提交和状态查询。</p>
 *
 * @author 乾乾
 * @see <a href="https://ai.gitee.com/docs/openapi/v1#tag/%E8%A7%86%E9%A2%91%E7%94%9F%E6%88%90/post/async/videos/generations">Gitee AI 视频生成文档</a>
 */
public class GiteeAiVideoApi {

	/**
	 * Gitee AI Base URL
	 */
	public static final String BASE_URL = "https://ai.gitee.com";

	private final RestClient restClient;

	public GiteeAiVideoApi(String apiKey) {
		this(BASE_URL, apiKey, RestClient.builder());
	}

	public GiteeAiVideoApi(String baseUrl, String apiKey) {
		this(baseUrl, apiKey, RestClient.builder());
	}

	public GiteeAiVideoApi(String baseUrl, String apiKey, RestClient.Builder restClientBuilder) {
		this(baseUrl, apiKey, restClientBuilder, RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);
	}

	public GiteeAiVideoApi(String baseUrl, String apiKey, RestClient.Builder restClientBuilder,
						   ResponseErrorHandler responseErrorHandler) {
		this(baseUrl, apiKey, CollectionUtils.toMultiValueMap(Map.of()), restClientBuilder, responseErrorHandler);
	}

	public GiteeAiVideoApi(String baseUrl, String apiKey, MultiValueMap<String, String> headers,
						   RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {
		this(baseUrl, new SimpleApiKey(apiKey), headers, restClientBuilder, responseErrorHandler);
	}

	public GiteeAiVideoApi(String baseUrl, ApiKey apiKey, MultiValueMap<String, String> headers,
						   RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {

		// @formatter:off
		this.restClient = restClientBuilder.baseUrl(baseUrl)
				.defaultHeaders(h -> {
					if (!(apiKey instanceof NoopApiKey)) {
						h.setBearerAuth(apiKey.getValue());
					}
					h.setContentType(MediaType.APPLICATION_JSON);
					h.addAll(headers);
				})
				.defaultStatusHandler(responseErrorHandler)
				.build();
		// @formatter:on
	}

	/**
	 * 提交视频生成任务
	 *
	 * @param request 视频生成请求
	 * @return 视频生成任务响应（包含任务ID）
	 */
	public ResponseEntity<GiteeAiVideoSubmitResponse> submitVideo(GiteeAiVideoRequest request) {
		Assert.notNull(request, "Video request cannot be null.");
		Assert.hasLength(request.prompt(), "Prompt cannot be empty.");

		return this.restClient.post()
				.uri("/v1/async/videos/generations")
				.body(request)
				.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM) // 接受 application/octet-stream
				.retrieve()
				.toEntity(GiteeAiVideoSubmitResponse.class);
	}

	/**
	 * 获取视频生成状态
	 * 官方文档：https://ai.gitee.com/docs/openapi/v1#tag/%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1/get/task/{task_id}/get
	 *
	 * 注意：查询接口返回的格式和提交接口不同！
	 *
	 * @param taskId 任务ID
	 * @return 视频生成状态响应
	 */
	public ResponseEntity<GiteeAiVideoStatusResponse> getVideoStatus(String taskId) {
		Assert.hasLength(taskId, "Task ID cannot be empty.");

		return this.restClient.get()
				.uri("/v1/task/{task_id}/get", taskId)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(GiteeAiVideoStatusResponse.class);
	}

	/**
	 * 视频生成请求
	 *
	 * @param model 模型名称（必填），例如：stepvideo-t2v
	 * @param prompt 提示词（必填）
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record GiteeAiVideoRequest(
			@JsonProperty("model") String model,
			@JsonProperty("prompt") String prompt) {
	}

	/**
	 * 视频生成任务提交响应
	 * 根据官方文档，提交后返回完整的异步任务实体
	 *
	 * @param taskId 任务唯一标识
	 * @param status 任务状态（waiting / in_progress / success / failure / cancelled）
	 * @param output 输出结果（仅在 status=success 时出现）
	 * @param createdAt 任务创建时间
	 * @param startedAt 任务开始处理时间（未开始时为空）
	 * @param completedAt 任务完成时间（未完成时为空）
	 * @param urls 任务相关链接
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record GiteeAiVideoSubmitResponse(
			@JsonProperty("task_id") String taskId,
			@JsonProperty("status") String status,
			@JsonProperty("output") VideoOutput output,
			@JsonProperty("created_at") String createdAt,
			@JsonProperty("started_at") String startedAt,
			@JsonProperty("completed_at") String completedAt,
			@JsonProperty("urls") TaskUrls urls) {
	}

	/**
	 * 视频生成状态查询响应
	 *
	 * 注意：Gitee AI 的查询接口和提交接口返回格式不同！
	 *
	 * 提交接口返回（完整的异步任务实体）：
	 * {
	 *   "task_id": "...",
	 *   "status": "waiting",
	 *   "created_at": "...",
	 *   "urls": {...}
	 * }
	 *
	 * 查询接口返回（仅包含视频URL）：
	 * {
	 *   "file_url": "https://..."
	 * }
	 *
	 * @param taskId 任务唯一标识（仅提交接口返回）
	 * @param status 任务状态（仅提交接口返回）
	 * @param output 输出结果（仅提交接口返回）
	 * @param createdAt 任务创建时间（仅提交接口返回）
	 * @param startedAt 任务开始处理时间（仅提交接口返回）
	 * @param completedAt 任务完成时间（仅提交接口返回）
	 * @param urls 任务相关链接（仅提交接口返回）
	 * @param fileUrl 视频文件URL（仅查询接口返回）
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record GiteeAiVideoStatusResponse(
			@JsonProperty("task_id") String taskId,
			@JsonProperty("status") String status,
			@JsonProperty("output") VideoOutput output,
			@JsonProperty("created_at") String createdAt,
			@JsonProperty("started_at") String startedAt,
			@JsonProperty("completed_at") String completedAt,
			@JsonProperty("urls") TaskUrls urls,
			@JsonProperty("file_url") String fileUrl) {
	}

	/**
	 * 视频输出结果
	 * 注意：链接仅 7 天有效，请及时下载保存
	 *
	 * @param videoUrl 视频URL
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record VideoOutput(
			@JsonProperty("video_url") String videoUrl) {
	}

	/**
	 * 任务相关链接
	 *
	 * @param get 查询任务链接
	 * @param cancel 取消任务链接
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record TaskUrls(
			@JsonProperty("get") String get,
			@JsonProperty("cancel") String cancel) {
	}
}