package com.hula.core.user.domain.vo.resp.ws;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMemberChange extends BaseEntity {
    public static final Integer CHANGE_TYPE_ADD = 1;
    public static final Integer CHANGE_TYPE_REMOVE = 2;
    @Schema(description ="群组id")
    private Long roomId;
    @Schema(description ="变动uid集合")
    private Long uid;
    @Schema(description ="变动类型 1加入群组 2移除群组")
    private Integer changeType;
    @Schema(description ="在线状态 1在线 2离线")
    private Integer activeStatus;
    @Schema(description ="最后一次上下线时间")
    private Date lastOptTime;
}
