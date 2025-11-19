package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("user_privacy")
public class UserPrivacy extends Entity<Long> {
	private static final long serialVersionUID = 1L;

	@Schema(description = "用户ID")
	private Long uid;

	@Schema(description = "是否私密账号（true-是，false-否）")
	private Boolean isPrivate;

	@Schema(description = "是否允许临时会话（true-允许，false-不允许）")
	private Boolean allowTempSession;

	@Schema(description = "是否允许通过手机号搜索（true-允许，false-不允许）")
	private Boolean searchableByPhone;

	@Schema(description = "是否允许通过账号搜索（true-允许，false-不允许）")
	private Boolean searchableByAccount;

	@Schema(description = "是否允许通过用户名搜索（true-允许，false-不允许）")
	private Boolean searchableByUsername;

	@Schema(description = "是否显示在线状态（true-显示，false-不显示）")
	private Boolean showOnlineStatus;

	@Schema(description = "是否允许添加好友（true-允许，false-不允许）")
	private Boolean allowAddFriend;

	@Schema(description = "是否允许群邀请（true-允许，false-不允许）")
	private Boolean allowGroupInvite;

	@Schema(description = "是否隐藏个人资料（true-隐藏，false-不隐藏）")
	private Boolean hideProfile;

	@Schema(description = "租户ID")
	private Long tenantId;
}