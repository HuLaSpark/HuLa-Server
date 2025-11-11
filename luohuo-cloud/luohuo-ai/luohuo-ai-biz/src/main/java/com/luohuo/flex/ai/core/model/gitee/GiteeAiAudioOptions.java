package com.luohuo.flex.ai.core.model.gitee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luohuo.flex.ai.core.model.audio.AudioOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gitee AI 音频生成参数配置类
 *
 * <p>实现 {@link AudioOptions} 接口，封装 Gitee AI 平台的音频生成参数。
 * Gitee AI 兼容 OpenAI 的 TTS 接口，支持文本转语音(TTS)功能。</p>
 *
 * @author 乾乾
 * @see AudioOptions
 * @see <a href="https://ai.gitee.com/docs">Gitee AI 文档</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiteeAiAudioOptions implements AudioOptions {

	/**
	 * 模型名称
	 * <p>Gitee AI 支持的 TTS 模型</p>
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * 音色/声音
	 * <p>支持的音色选项，例如：alloy, echo, fable, onyx, nova, shimmer</p>
	 */
	@JsonProperty("voice")
	private String voice;

	/**
	 * 语速
	 * <p>范围：0.25 - 4.0，默认 1.0</p>
	 */
	@JsonProperty("speed")
	private Double speed;

	/**
	 * 音频输出格式
	 * <p>支持的格式：mp3, opus, aac, flac, wav, pcm</p>
	 */
	@JsonProperty("response_format")
	private String responseFormat;

	/**
	 * 创建默认配置
	 *
	 * @param model 模型名称
	 * @return 默认配置
	 */
	public static GiteeAiAudioOptions createDefault(String model) {
		return GiteeAiAudioOptions.builder()
				.model(model)
				.voice("alloy")
				.speed(1.0)
				.responseFormat("mp3")
				.build();
	}
}