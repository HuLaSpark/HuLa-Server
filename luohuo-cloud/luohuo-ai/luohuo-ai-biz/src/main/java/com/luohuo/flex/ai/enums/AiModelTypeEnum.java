package com.luohuo.flex.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * AI 模型类型的枚举
 *
 */
@Getter
@RequiredArgsConstructor
public enum AiModelTypeEnum implements ArrayValuable<Integer> {

    CHAT(1, "对话"),
    IMAGE(2, "图片"),
    AUDIO(3, "音频"),
    EMBEDDING(5, "向量"),
    RERANK(6, "重排序"),
    TEXT_TO_VIDEO(7, "文生视频"),
    IMAGE_TO_VIDEO(8, "图生视频");

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiModelTypeEnum::getType).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
