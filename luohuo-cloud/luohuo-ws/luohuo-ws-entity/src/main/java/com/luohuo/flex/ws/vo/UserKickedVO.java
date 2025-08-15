package com.luohuo.flex.ws.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户被踢出通知
 */
@Data
@AllArgsConstructor
public class UserKickedVO {
    private Long roomId;       // 房间ID
    private Long kickedUid;  // 被踢用户ID
    private Long operatorId;   // 操作者ID
    private String reason;     // 踢出原因
}
