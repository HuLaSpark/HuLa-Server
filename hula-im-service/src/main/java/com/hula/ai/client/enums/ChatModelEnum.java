package com.hula.ai.client.enums;

import lombok.Getter;

/**
 * 聊天大模型枚举类
 *
 * @author: 云裂痕
 * @date: 2025/03/07
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Getter
public enum ChatModelEnum {

    /**
     * CHAT_GPT
     */
    OPENAI("ChatGPT", "ChatGPT"),

    WENXIN("WENXIN", "文心一言"),

    CHATGLM("ChatGLM", "智谱清言"),

    TONGYI("QIANWEN", "通义千问"),

    SPARK("SPARK", "讯飞星火"),

    MOONSHOT("Moonshot", "月之暗面"),

    DEEPSEEK("DeepSeek", "深度求索"),

    DOUBAO("Doubao", "豆包"),

    INTERNLM("Internlm", "书生·浦语"),

    LOCALLM("LocalLM", "本地模型"),

    ;

    /**
     * 值
     */
    private final String value;

    /**
     * 标签
     */
    private final String label;

    ChatModelEnum(final String value, final String label) {
        this.label = label;
        this.value = value;
    }

    public static ChatModelEnum getEnum(String value) {
        for (ChatModelEnum chatModelEnum : ChatModelEnum.values()) {
            if (value.equals(chatModelEnum.value)) {
                return chatModelEnum;
            }
        }
        return null;
    }

}
