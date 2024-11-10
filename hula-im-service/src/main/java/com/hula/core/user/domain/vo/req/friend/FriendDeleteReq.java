package com.hula.core.user.domain.vo.req.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;


/**
 * 申请好友信息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendDeleteReq {

    @NotNull
    @Schema(description ="好友uid")
    private Long targetUid;

}
