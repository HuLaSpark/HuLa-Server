package com.hula.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nyh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgSendMessageDTO implements Serializable {
    /**
     * 消息id
     */
    private Long msgId;
    /**
     * 操作人uid
     */
    private Long uid;
}
