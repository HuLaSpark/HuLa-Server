package com.luohuo.flex.im.domain.vo.resp.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


/**
 * 好友校验
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendResp implements Serializable {

    @Schema(description ="好友uid")
    private Long uid;

	@Schema(description ="好友备注")
	private String remark;

    @Schema(description ="在线状态 1在线 2离线")
    private Integer activeStatus;

	@Schema(description ="不让他看我（0-允许，1-禁止）")
	private Boolean hideMyPosts;

	@Schema(description ="不看他（0-允许，1-禁止）")
	private Boolean hideTheirPosts;

	@Schema(description ="好友用户名")
	private String name;

	@Schema(description ="好友账号")
	private String account;

	@Schema(description ="好友头像")
	private String avatar;
}
