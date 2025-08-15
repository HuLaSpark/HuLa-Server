package com.luohuo.flex.im.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息状态
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {
    TEXT(1, "正常消息"),
    RECALL(2, "撤回消息"),
    IMG(3, "图片"),
    FILE(4, "文件"),
    SOUND(5, "语音"),
    VIDEO(6, "视频"),
    EMOJI(7, "表情"),
    SYSTEM(8, "系统消息"),
	MERGE(9, "合并消息"),
	NOTICE(10, "公告消息"),
	BOT(11, "机器人"),
	VIDEO_CALL(12, "视频电话消息"),
	AUDIO_CALL(13, "音频电话消息"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, MessageTypeEnum> cache;

    static {
        cache = Arrays.stream(MessageTypeEnum.values()).collect(Collectors.toMap(MessageTypeEnum::getType, Function.identity()));
    }

    public static MessageTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
