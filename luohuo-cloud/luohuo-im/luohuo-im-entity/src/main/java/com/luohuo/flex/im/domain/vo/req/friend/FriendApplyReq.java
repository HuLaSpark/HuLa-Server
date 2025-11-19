package com.luohuo.flex.im.domain.vo.req.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


/**
 * 申请好友信息
 * @author nyh
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyReq implements Serializable {

    @NotBlank
    @Schema(description ="申请信息")
    private String msg;

    @NotNull
    @Schema(description ="好友uid")
    private Long targetUid;
}
