package com.hula.common.domain.dto;

import com.hula.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nyh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgSendMessageDTO extends BaseEntity {
    /**
     * 消息id
     */
    private Long msgId;
    /**
     * 操作人uid
     */
    private Long uid;
}
