package com.luohuo.flex.im.domain.vo.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

/**
 * 移除群成员
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAddReq {
    @NotNull
    @Schema(description ="房间id")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 50)
    @Schema(description ="邀请的uid")
    private HashSet<Long> uidList;
}
