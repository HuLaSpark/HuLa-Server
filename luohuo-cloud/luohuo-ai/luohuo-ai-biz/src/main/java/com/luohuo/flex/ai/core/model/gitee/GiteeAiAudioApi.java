package com.luohuo.flex.ai.core.model.gitee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/**
 * Gitee AI 音频生成 API 客户端
 *
 * <p>官方文档：https://ai.gitee.com/docs/openapi/v1#tag/%E9%9F%B3%E9%A2%91/post/audio/speech</p>
 *
 * <p>注意：Gitee AI 的音频接口和 OpenAI 不完全兼容，主要区别：
 * <ul>
 *   <li>没有 voice 参数（不支持选择音色）</li>
 *   <li>使用 response_data_format 而不是 response_format</li>
 *   <li>返回格式为 blob（二进制数据）</li>
 * </ul>
 * </p>
 *
 * @author 乾乾
 */
@Slf4j
public class GiteeAiAudioApi {

	/**
	 * Gitee AI Base URL
	 */
	public static final String BASE_URL = "https://ai.gitee.com/v1";

	/**
	 * RestClient 实例
	 */
	private final RestClient restClient;

	/**
	 * 构造函数
	 *
	 * @param apiKey Gitee AI API Key
	 */
	public GiteeAiAudioApi(String apiKey) {
		this(BASE_URL, apiKey);
	}

	/**
	 * 构造函数（支持自定义 Base URL）
	 *
	 * @param baseUrl Base URL
	 * @param apiKey Gitee AI API Key
	 */
	public GiteeAiAudioApi(String baseUrl, String apiKey) {
		Assert.hasLength(baseUrl, "Base URL cannot be empty");
		Assert.hasLength(apiKey, "API Key cannot be empty");

		this.restClient = RestClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	/**
	 * 生成语音
	 *
	 * <p>官方文档：https://ai.gitee.com/docs/openapi/v1#tag/%E9%9F%B3%E9%A2%91/post/audio/speech</p>
	 *
	 * @param request 音频生成请求
	 * @return 音频数据（字节数组）
	 */
	public byte[] createSpeech(AudioRequest request) {
		Assert.notNull(request, "Request cannot be null");
		Assert.hasLength(request.getModel(), "Model cannot be empty");
		Assert.hasLength(request.getInput(), "Input cannot be empty");

		log.info("[GiteeAiAudioApi] 生成语音，model={}, input={}", request.getModel(), request.getInput());

		ResponseEntity<byte[]> response = this.restClient.post()
				.uri("/audio/speech")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM)  // 接受二进制数据
				.body(request)
				.retrieve()
				.toEntity(byte[].class);

		if (response.getBody() == null) {
			throw new RuntimeException("生成语音失败，响应为空");
		}

		log.info("[GiteeAiAudioApi] 生成语音成功，大小={}字节", response.getBody().length);
		return response.getBody();
	}

	/**
	 * 音频生成请求
	 *
	 * <p>官方文档示例：
	 * <pre>
	 * {
	 *   "model": "Duix.Heygem",
	 *   "input": "你好，欢迎使用AI语音合成",
	 *   "response_data_format": "blob"
	 * }
	 * </pre>
	 * </p>
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AudioRequest {
		/**
		 * 模型名称
		 * <p>例如：Duix.Heygem</p>
		 */
		@JsonProperty("model")
		private String model;

		/**
		 * 输入文本
		 * <p>要转换为语音的文本内容</p>
		 */
		@JsonProperty("input")
		private String input;

		/**
		 * 响应数据格式
		 * <p>固定为 "blob"（二进制数据）</p>
		 */
		@JsonProperty("response_data_format")
		private String responseDataFormat;

		/**
		 * 构造函数（使用默认响应格式）
		 *
		 * @param model 模型名称
		 * @param input 输入文本
		 */
		public AudioRequest(String model, String input) {
			this.model = model;
			this.input = input;
			this.responseDataFormat = "blob";  // 默认为 blob
		}
	}
}
