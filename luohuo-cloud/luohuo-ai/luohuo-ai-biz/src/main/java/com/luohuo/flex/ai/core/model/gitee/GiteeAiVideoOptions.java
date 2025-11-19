package com.luohuo.flex.ai.core.model.gitee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luohuo.flex.ai.core.model.video.VideoOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gitee AI 视频生成参数配置类
 *
 * <p>实现 {@link VideoOptions} 接口，封装 Gitee AI 平台的视频生成参数。
 * Gitee AI 支持文本生成视频(T2V)功能。</p>
 *
 * @author 乾乾
 * @see VideoOptions
 * @see <a href="https://ai.gitee.com/docs/openapi/v1#tag/%E8%A7%86%E9%A2%91%E7%94%9F%E6%88%90/post/async/videos/generations">Gitee AI 视频生成文档</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiteeAiVideoOptions implements VideoOptions {

	/**
	 * 模型名称
	 * <p>Gitee AI 支持的视频生成模型，例如：stepvideo-t2v</p>
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * 提示词（必填）
	 * <p>描述要生成的视频内容</p>
	 */
	@JsonProperty("prompt")
	private String prompt;

	/**
	 * 视频宽度（像素）
	 * <p>视频的水平分辨率</p>
	 */
	private Integer width;

	/**
	 * 视频高度（像素）
	 * <p>视频的垂直分辨率</p>
	 */
	private Integer height;

	/**
	 * 视频时长（秒）
	 * <p>生成视频的时长</p>
	 */
	private Integer duration;

	/**
	 * 图片尺寸（用于内部计算）
	 * <p>根据宽高计算得出，例如：1280x720, 720x1280, 960x960</p>
	 */
	private String imageSize;

	/**
	 * 负面提示词
	 * <p>描述不希望出现在视频中的内容</p>
	 */
	private String negativePrompt;

	/**
	 * 随机种子
	 * <p>用于控制生成结果的随机性</p>
	 */
	private Integer seed;

	/**
	 * 图片URL或base64（用于图片生成视频 I2V）
	 * <p>如果提供，则基于该图片生成视频</p>
	 */
	private String image;

	/**
	 * 创建默认配置
	 *
	 * @param model 模型名称
	 * @return 默认配置
	 */
	public static GiteeAiVideoOptions createDefault(String model) {
		return GiteeAiVideoOptions.builder()
				.model(model)
				.build();
	}
}
