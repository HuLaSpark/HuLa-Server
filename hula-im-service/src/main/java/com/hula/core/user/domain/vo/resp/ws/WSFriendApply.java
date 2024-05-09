package com.hula.core.user.domain.vo.resp.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSFriendApply {
    @Schema(description ="申请人")
    private Long uid;
    @Schema(description ="申请未读数")
    private Integer unreadCount;
}
