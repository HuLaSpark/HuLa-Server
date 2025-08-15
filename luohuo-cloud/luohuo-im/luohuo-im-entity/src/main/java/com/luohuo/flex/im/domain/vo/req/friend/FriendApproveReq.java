package com.luohuo.flex.im.domain.vo.req.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import com.luohuo.basic.base.entity.BaseEntity;


/**
 * 申请好友信息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApproveReq extends BaseEntity {

    @NotNull
    @Schema(description ="申请id")
    private Long applyId;

}
