package com.luohuo.flex.ai.core.model.silicon;

import com.luohuo.flex.ai.core.model.audio.AudioModel;
import com.luohuo.flex.ai.core.model.audio.AudioOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * 硅基流动音频生成模型实现类
 *
 * <p>实现 {@link AudioModel} 接口，封装硅基流动平台的音频生成功能。
 * 支持文本转语音(TTS)，可配置不同的音色、语速等参数。</p>
 *
 * @author 乾乾
 * @see AudioModel
 * @see SiliconFlowAudioOptions
 * @see SiliconFlowAudioApi
 */
@Slf4j
public class SiliconFlowAudioModel implements AudioModel {

	/**
	 * 硅基流动音频API客户端
	 */
	private final SiliconFlowAudioApi siliconFlowAudioApi;

	/**
	 * 默认配置参数
	 */
	private final SiliconFlowAudioOptions defaultOptions;

	/**
	 * 构造函数 - 使用默认配置
	 *
	 * @param siliconFlowAudioApi 硅基流动音频API客户端
	 */
	public SiliconFlowAudioModel(SiliconFlowAudioApi siliconFlowAudioApi) {
		this(siliconFlowAudioApi, SiliconFlowAudioOptions.builder()
				.speed(1.0)
				.responseFormat("mp3")
				.build());
	}

	/**
	 * 构造函数 - 使用自定义默认配置
	 *
	 * @param siliconFlowAudioApi 硅基流动音频API客户端
	 * @param defaultOptions 默认配置参数
	 */
	public SiliconFlowAudioModel(SiliconFlowAudioApi siliconFlowAudioApi, SiliconFlowAudioOptions defaultOptions) {
		Assert.notNull(siliconFlowAudioApi, "SiliconFlowAudioApi must not be null");
		Assert.notNull(defaultOptions, "defaultOptions must not be null");
		this.siliconFlowAudioApi = siliconFlowAudioApi;
		this.defaultOptions = defaultOptions;
	}

	/**
	 * 生成语音
	 *
	 * @param prompt 文本内容
	 * @param options 音频生成参数
	 * @return 音频数据（字节数组）
	 */
	@Override
	public byte[] generateSpeech(String prompt, AudioOptions options) {
		Assert.hasLength(prompt, "Prompt cannot be empty");

		// 转换为硅基流动的参数格式
		SiliconFlowAudioOptions siliconOptions = convertToSiliconFlowOptions(options);

		// 合并默认参数和运行时参数
		SiliconFlowAudioOptions mergedOptions = mergeOptions(siliconOptions);

		// 构建请求对象
		SiliconFlowAudioApi.AudioRequest request = new SiliconFlowAudioApi.AudioRequest(
				mergedOptions.getModel(),
				prompt,
				mergedOptions.getVoice(),
				mergedOptions.getResponseFormat(),
				mergedOptions.getSpeed()
		);

		// 调用API生成音频
		return siliconFlowAudioApi.createSpeech(request);
	}

	/**
	 * 获取支持的音色列表
	 *
	 * @return 支持的音色集合，如果模型不支持则返回null
	 */
	@Override
	public Set<String> getSupportedVoices() {
		if (defaultOptions.getModel() != null) {
			return SiliconFlowAudioApi.getSupportedVoices(defaultOptions.getModel());
		}
		return null;
	}

	/**
	 * 将通用的 AudioOptions 转换为硅基流动的参数格式
	 *
	 * @param options 通用音频参数
	 * @return 硅基流动音频参数
	 */
	private SiliconFlowAudioOptions convertToSiliconFlowOptions(AudioOptions options) {
		if (options instanceof SiliconFlowAudioOptions) {
			return (SiliconFlowAudioOptions) options;
		}

		if (options == null) {
			return SiliconFlowAudioOptions.builder().build();
		}

		return SiliconFlowAudioOptions.builder()
				.model(options.getModel())
				.voice(options.getVoice())
				.speed(options.getSpeed())
				.responseFormat(options.getResponseFormat())
				.build();
	}

	/**
	 * 合并默认参数和运行时参数
	 * <p>运行时参数优先级高于默认参数</p>
	 *
	 * @param runtimeOptions 运行时参数
	 * @return 合并后的参数
	 */
	private SiliconFlowAudioOptions mergeOptions(SiliconFlowAudioOptions runtimeOptions) {
		if (runtimeOptions == null) {
			return defaultOptions;
		}

		return SiliconFlowAudioOptions.builder()
				.model(runtimeOptions.getModel() != null ? runtimeOptions.getModel() : defaultOptions.getModel())
				.voice(runtimeOptions.getVoice() != null ? runtimeOptions.getVoice() : defaultOptions.getVoice())
				.speed(runtimeOptions.getSpeed() != null ? runtimeOptions.getSpeed() : defaultOptions.getSpeed())
				.responseFormat(runtimeOptions.getResponseFormat() != null ? runtimeOptions.getResponseFormat() : defaultOptions.getResponseFormat())
				.build();
	}
}
