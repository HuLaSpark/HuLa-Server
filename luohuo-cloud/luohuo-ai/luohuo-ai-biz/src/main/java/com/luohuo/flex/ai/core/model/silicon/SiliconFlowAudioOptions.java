package com.luohuo.flex.ai.core.model.silicon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luohuo.flex.ai.core.model.audio.AudioOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 硅基流动音频生成参数配置类
 *
 * <p>实现 {@link AudioOptions} 接口，封装硅基流动平台的音频生成参数。
 * 支持文本转语音(TTS)功能，可配置音色、语速、输出格式等参数。</p>
 *
 * <p>支持的模型：</p>
 * <ul>
 *   <li>fnlp/MOSS-TTSD-v0.5 - 支持多种音色</li>
 *   <li>CosyVoice2-0.5B - 支持默认音色</li>
 * </ul>
 *
 * @author 乾乾
 * @see AudioOptions
 * @see <a href="https://docs.siliconflow.cn/cn/api-reference/audio/speech">硅基流动音频API文档</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiliconFlowAudioOptions implements AudioOptions {

	/**
	 * 模型名称
	 * <p>支持的模型：fnlp/MOSS-TTSD-v0.5, CosyVoice2-0.5B</p>
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * 音色/声音
	 * <p>不同模型支持不同的音色：</p>
	 * <ul>
	 *   <li>MOSS-TTSD-v0.5: alex, anna, bella, benjamin, charles, claire, david, diana</li>
	 *   <li>CosyVoice2-0.5B: default</li>
	 * </ul>
	 */
	@JsonProperty("voice")
	private String voice;

	/**
	 * 语速
	 * <p>范围：0.25 - 4.0，默认值：1.0</p>
	 * <ul>
	 *   <li>小于1.0：减慢语速</li>
	 *   <li>等于1.0：正常语速</li>
	 *   <li>大于1.0：加快语速</li>
	 * </ul>
	 */
	@JsonProperty("speed")
	private Double speed;

	/**
	 * 音频输出格式
	 * <p>支持的格式：mp3, wav, flac, opus</p>
	 * <p>默认值：mp3</p>
	 */
	@JsonProperty("response_format")
	private String responseFormat;

	/**
	 * 获取默认音色
	 *
	 * @param model 模型名称
	 * @return 默认音色
	 */
	public static String getDefaultVoiceForModel(String model) {
		if ("fnlp/MOSS-TTSD-v0.5".equals(model)) {
			return "fnlp/MOSS-TTSD-v0.5:anna";
		} else if ("CosyVoice2-0.5B".equals(model) || "FunAudioLLM/CosyVoice2-0.5B".equals(model)) {
			return "default";
		}
		return "default";
	}

	/**
	 * 创建默认配置
	 *
	 * @param model 模型名称
	 * @return 默认配置
	 */
	public static SiliconFlowAudioOptions createDefault(String model) {
		return SiliconFlowAudioOptions.builder()
				.model(model)
				.voice(getDefaultVoiceForModel(model))
				.speed(1.0)
				.responseFormat("mp3")
				.build();
	}
}
