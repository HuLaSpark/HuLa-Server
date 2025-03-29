package com.hula.ai.llm.locallm.coze.enums;

import lombok.Getter;

/**
 * 消息类型
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Getter
public enum MessageTypeEnum {

    /**
     * 消息内容类型
     */
    QUESTION("question", "用户输入内容"),
    ANSWER("answer", "Bot 返回给用户的消息内容"),
    FUNCTION_CALL("function_call", "Bot 对话过程中调用函数（function call）的中间结果"),
    TOOL_OUTPUT("tool_output", "调用工具 （function call）后返回的结果"),
    TOOL_RESPONSE("tool_response", "调用工具 （function call）后返回的结果"),
    FOLLOW_UP("follow_up", "推荐问题"),
    VERBOSE("verbose", "多 answer 场景下，服务端会返回一个 verbose 包"),

    ;

    /**
     * 值
     */
    private final String value;

    /**
     * 标签
     */
    private final String label;

    MessageTypeEnum(final String value, final String label) {
        this.value = value;
        this.label = label;
    }

}
