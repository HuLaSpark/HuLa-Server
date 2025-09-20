package com.luohuo.flex.model.entity.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSNotice implements Serializable {
    @Schema(description ="申请人、被邀请人")
    private Long uid;
	@Schema(description ="好友申请列表的未读数")
	private Integer unReadCount4Friend;
	@Schema(description ="群聊申请列表的未读数")
	private Integer unReadCount4Group;
}
