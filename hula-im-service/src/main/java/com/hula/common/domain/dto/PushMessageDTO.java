package com.hula.common.domain.dto;

import com.hula.core.user.domain.enums.WSBaseResp;
import com.hula.core.user.domain.enums.WSPushTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 推送给用户的消息对象
 * @author nyh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushMessageDTO implements Serializable {
    /**
     * 推送的ws消息
     */
    private WSBaseResp<?> wsBaseMsg;
    /**
     * 推送的uid
     */
    private List<Long> uidList;

    /**
     * 推送类型 1个人 2全员
     */
    private Integer pushType;

    /**
     * 操作人uid
     */
    private Long uid;

    public PushMessageDTO(Long uid, WSBaseResp<?> wsBaseMsg, Long cuid) {
        this.uidList = Collections.singletonList(uid);
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
        this.uid = cuid;
    }

    public PushMessageDTO(List<Long> uidList, WSBaseResp<?> wsBaseMsg, Long uid) {
        this.uidList = uidList;
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
        this.uid = uid;
    }

    public PushMessageDTO(WSBaseResp<?> wsBaseMsg, Long uid) {
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.ALL.getType();
        this.uid = uid;
    }
}
