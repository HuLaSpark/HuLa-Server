package com.hula.core.user.domain.vo.req.friend;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 申请好友信息
 * @author nyh
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyReq extends BaseEntity {

    @NotBlank
    @Schema(description ="申请信息")
    private String msg;

    @NotNull
    @Schema(description ="好友uid")
    private Long targetUid;
}
