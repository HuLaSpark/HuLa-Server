package com.hula.core.user.domain.vo.resp.ws;

import com.hula.domain.BaseEntity;
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
public class WSFriendApproval extends BaseEntity {
    @Schema(description ="申请人")
    private Long uid;
}
