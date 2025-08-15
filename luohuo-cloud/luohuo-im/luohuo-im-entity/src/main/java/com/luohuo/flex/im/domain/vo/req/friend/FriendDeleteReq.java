package com.luohuo.flex.im.domain.vo.req.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luohuo.basic.base.entity.BaseEntity;


/**
 * 申请好友信息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendDeleteReq extends BaseEntity {

    @NotNull
    @Schema(description ="好友uid")
    private Long targetUid;

}
