package com.luohuo.flex.ai.core.model.silicon;

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
 * 硅基流动 Video API
 *
 * @see <a href= "https://docs.siliconflow.cn/cn/api-reference/videos/videos_submit">Videos</a>
 */
public class SiliconFlowVideoApi {

	private final RestClient restClient;

	public SiliconFlowVideoApi(String aiToken) {
		this(SiliconFlowApiConstants.DEFAULT_BASE_URL, aiToken, RestClient.builder());
	}

	public SiliconFlowVideoApi(String baseUrl, String openAiToken) {
		this(baseUrl, openAiToken, RestClient.builder());
	}

	public SiliconFlowVideoApi(String baseUrl, String openAiToken, RestClient.Builder restClientBuilder) {
		this(baseUrl, openAiToken, restClientBuilder, RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);
	}

	public SiliconFlowVideoApi(String baseUrl, String apiKey, RestClient.Builder restClientBuilder,
							   ResponseErrorHandler responseErrorHandler) {
		this(baseUrl, apiKey, CollectionUtils.toMultiValueMap(Map.of()), restClientBuilder, responseErrorHandler);
	}

	public SiliconFlowVideoApi(String baseUrl, String apiKey, MultiValueMap<String, String> headers,
							   RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {
		this(baseUrl, new SimpleApiKey(apiKey), headers, restClientBuilder, responseErrorHandler);
	}

	public SiliconFlowVideoApi(String baseUrl, ApiKey apiKey, MultiValueMap<String, String> headers,
							   RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {

// @formatter:off
this.restClient = restClientBuilder.baseUrl(baseUrl)
.defaultHeaders(h -> {
if(!(apiKey instanceof NoopApiKey)) {
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
	 */
	public ResponseEntity<SiliconflowVideoSubmitResponse> submitVideo(SiliconflowVideoRequest siliconflowVideoRequest) {
		Assert.notNull(siliconflowVideoRequest, "Video request cannot be null.");
		Assert.hasLength(siliconflowVideoRequest.prompt(), "Prompt cannot be empty.");

		return this.restClient.post()
				.uri("v1/video/submit")
				.body(siliconflowVideoRequest)
				.retrieve()
				.toEntity(SiliconflowVideoSubmitResponse.class);
	}

	/**
	 * 获取视频生成状态
	 */
	public ResponseEntity<SiliconflowVideoStatusResponse> getVideoStatus(String requestId) {
		Assert.hasLength(requestId, "Request ID cannot be empty.");

		return this.restClient.post()
				.uri("v1/video/status")
				.body(new SiliconflowVideoStatusRequest(requestId))
				.retrieve()
				.toEntity(SiliconflowVideoStatusResponse.class);
	}


	// @formatter:off
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record SiliconflowVideoRequest (
			@JsonProperty("prompt") String prompt,
			@JsonProperty("model") String model,
			@JsonProperty("image_size") String imageSize,
			@JsonProperty("negative_prompt") String negativePrompt,
			@JsonProperty("image") String image,
			@JsonProperty("seed") Integer seed) {

		public SiliconflowVideoRequest(String prompt, String model, String imageSize) {
			this(prompt, model, imageSize, null, null, null);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record SiliconflowVideoSubmitResponse (
			@JsonProperty("requestId") String requestId) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record SiliconflowVideoStatusRequest (
			@JsonProperty("requestId") String requestId) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record SiliconflowVideoStatusResponse (
			@JsonProperty("status") String status,
			@JsonProperty("videos") java.util.List<VideoResult> videos,
			@JsonProperty("message") String message) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record VideoResult (
			@JsonProperty("url") String url,
			@JsonProperty("seed") Integer seed) {
	}

}