package com.luohuo.flex.im.domain.vo.req.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingInviteResp implements Serializable {
	@Schema(description ="邀请人id")
    private Long inviteId;
	@Schema(description ="群id")
    private Long groupId;
	@Schema(description ="被邀请人id")
    private Long inviterUid;
	@Schema(description ="邀请时间")
    private LocalDateTime inviteTime;
}