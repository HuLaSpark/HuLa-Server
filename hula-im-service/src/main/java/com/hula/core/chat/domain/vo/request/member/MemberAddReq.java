package com.hula.core.chat.domain.vo.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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
    private List<Long> uidList;
}
