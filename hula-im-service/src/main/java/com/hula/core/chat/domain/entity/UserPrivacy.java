package com.hula.core.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_privacy")
public class UserPrivacy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键ID")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

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

	@Schema(description = "创建时间")
	private LocalDateTime createTime;

	@Schema(description = "更新时间")
	private LocalDateTime updateTime;

	@Schema(description = "租户ID")
	private Long tenantId;
}