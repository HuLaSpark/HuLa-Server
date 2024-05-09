package com.hula.core.chat.domain.vo.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 退出群聊
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberExitReq {
    @NotNull
    @Schema(description ="会话id")
    private Long roomId;
}
