package com.hula.ai.llm.base.entity;

import com.hula.ai.client.enums.ChatContentEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流返回内容
 *
 * @author: 云裂痕
 * @date: 2023/11/24
 * 得其道
 * 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatData {

    /**
     * 会话id
     */
    private String id;

    /**
     * 当前消息id
     */
    private String conversationId;

    /**
     * 回复消息id
     */
    private String parentMessageId;

    /**
     * 角色 system、user、assistant
     */
    private String role;

    /**
     * 内容
     */
    private Object content;

    /**
     * 是否输出结束
     */
    private Boolean finish;

    /**
     * 消息类型 text、image
     */
    @Builder.Default
    private String contentType = ChatContentEnum.TEXT.getValue();

}
