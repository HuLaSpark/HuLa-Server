package com.hula.core.websocket.domain.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMemberChange {
    public static final Integer CHANGE_TYPE_ADD = 1;
    public static final Integer CHANGE_TYPE_REMOVE = 2;
    @Schema(description = "群组id")
    private Long roomId;
    @Schema(description = "变动uid集合")
    private Long uid;
    @Schema(description = "变动类型 1加入群组 2移除群组")
    private Integer changeType;
    @Schema(description = "在线状态 1在线 2离线")
    private Integer activeStatus;
    @Schema(description = "最后一次上下线时间")
    private Date lastOptTime;
}
