package com.luohuo.flex.model.entity.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingInviteWs implements Serializable {
	@Schema(description ="邀请人id")
    private String inviteId;
	@Schema(description ="群id")
    private String groupId;
	@Schema(description ="被邀请人id")
    private String inviterUid;
	@Schema(description ="邀请时间")
    private LocalDateTime inviteTime;
}