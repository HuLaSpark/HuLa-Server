package com.hula.ai.client.enums;

import lombok.Getter;

/**
 * 聊天内容类型枚举类
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Getter
public enum ChatContentEnum {

    /**
     * TEXT
     */
    TEXT("text", "文字"),

    IMAGE("image", "图片"),

    VOICE("voice", "音频"),

    VIDEO("video", "视频"),

    FILE("file", "文件"),

    ;

    /**
     * 值
     */
    private final String value;

    /**
     * 标签
     */
    private final String label;

    ChatContentEnum(final String value, final String label) {
        this.label = label;
        this.value = value;
    }

    public static ChatContentEnum getEnum(String value) {
        for (ChatContentEnum chatModelEnum : ChatContentEnum.values()) {
            if (value.equals(chatModelEnum.value)) {
                return chatModelEnum;
            }
        }
        return null;
    }

}
