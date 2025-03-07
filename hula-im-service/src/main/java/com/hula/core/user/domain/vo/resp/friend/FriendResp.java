package com.hula.core.user.domain.vo.resp.friend;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 好友校验
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendResp extends BaseEntity {

    @Schema(description ="好友uid")
    private Long uid;

    @Schema(description ="在线状态 1在线 2离线")
    private Integer activeStatus;

	@Schema(description ="不让他看我（0-允许，1-禁止）")
	private Boolean hideMyPosts;

	@Schema(description ="不看他（0-允许，1-禁止）")
	private Boolean hideTheirPosts;
}
