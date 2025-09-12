package com.luohuo.flex.model.entity.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 成员列表的成员信息
 *
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp implements Serializable {

	@Schema(description = "群成员表id")
	private Long id;

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "账号")
	private String account;

    @Schema(description = "在线状态 1在线 2离线")
    private Integer activeStatus;

    @Schema(description = "角色ID")
    private Integer roleId;

	@Schema(description = "IP归属地")
	private String locPlace;

    @Schema(description = "最后一次上下线时间")
    private LocalDateTime lastOptTime;

	@Schema(description = "我的群昵称")
	private String myName;

	@Schema(description = "用户昵称")
	private String name;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "用户状态id")
	private Long userStateId;

	@Schema(description = "用户拥有的徽章id列表")
	private List<Long> itemIds;

	@Schema(description = "佩戴的徽章id")
	private Long wearingItemId;

	/**
	 * 用户类型 UserTypeEnum
	 * 参见 {@link com.luohuo.flex.im.enums.UserTypeEnum}
	 */
	@Schema(description = "用户类型")
	private Integer userType;
}
