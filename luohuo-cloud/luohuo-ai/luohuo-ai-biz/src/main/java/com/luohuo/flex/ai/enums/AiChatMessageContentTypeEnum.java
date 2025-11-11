package com.luohuo.flex.ai.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 聊天消息内容类型枚举
 *
 * <p>用于标记消息内容的类型，方便前端渲染</p>
 *
 * @author 乾乾
 */
@Getter
@AllArgsConstructor
public enum AiChatMessageContentTypeEnum implements ArrayValuable<Integer> {

	/**
	 * 文本
	 */
	TEXT(1, "文本"),

	/**
	 * 图片
	 */
	IMAGE(2, "图片"),

	/**
	 * 视频
	 */
	VIDEO(3, "视频"),

	/**
	 * 音频
	 */
	AUDIO(4, "音频");

	public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiChatMessageContentTypeEnum::getType).toArray(Integer[]::new);

	/**
	 * 类型
	 */
	private final Integer type;

	/**
	 * 描述
	 */
	private final String desc;

	@Override
	public Integer[] array() {
		return ARRAYS;
	}

	/**
	 * 根据类型获取枚举
	 *
	 * @param type 类型
	 * @return 枚举
	 */
	public static AiChatMessageContentTypeEnum valueOf(Integer type) {
		return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
	}

}
