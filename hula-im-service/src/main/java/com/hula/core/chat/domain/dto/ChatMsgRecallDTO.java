package com.hula.core.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息撤回的推送类
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgRecallDTO {
    private Long msgId;
    private Long roomId;
    //撤回的用户
    private Long recallUid;
}
