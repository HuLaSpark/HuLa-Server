package com.luohuo.flex.ws.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 全体静音状态
 */
@Data
@AllArgsConstructor
public class AllMutedVO {
    private Long roomId;       // 房间ID
    private boolean muted;     // 是否静音
    private Long operatorId;   // 操作者ID
}