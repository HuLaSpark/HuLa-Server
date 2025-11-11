package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.luohuo.basic.base.entity.Entity;
import com.luohuo.flex.im.enums.UserTypeEnum;
import com.luohuo.flex.model.entity.base.IpInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户表
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "im_user", autoResultMap = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户表")
public class User extends Entity<Long> {

	@Serial
	private static final long serialVersionUID = 1L;

	public static Long UID_SYSTEM = 1L;//系统uid

	/**
	 * DefUser用户id
	 */
	private Long userId;

	/**
	 * 用户账号
	 */
	@TableField("account")
	private String account;

	/**
	 * 邮箱
	 */
	@TableField("email")
	private String email;

	/**
	 * 用户昵称
	 */
	@TableField("name")
	private String name;

	/**
	 * 用户头像
	 */
	@TableField("avatar")
	private String avatar;

	/**
	 * 性别 1为男性，2为女性
	 */
	@TableField("sex")
	private Integer sex;

	/**
	 * 微信openid用户标识
	 */
	@TableField("open_id")
	private String openId;

	/**
	 * 个人简介
	 */
	@TableField("resume")
	private String resume;

	/**
	 * @see UserState
	 */
	@Schema(description = "用户状态id")
	private Long userStateId;

	/**
	 * 最后上下线时间
	 */
	@TableField("last_opt_time")
	private LocalDateTime lastOptTime;

	/**
	 * 佩戴的徽章id
	 */
	@TableField("item_id")
	private Long itemId;

	/**
	 * 用户状态 0正常 1拉黑
	 */
	@TableField("state")
	private Integer state;
	/**
	 * 修改头像时间
	 */
	@TableField("avatar_update_time")
	private LocalDateTime avatarUpdateTime;

	@Schema(description = "是否开启上下文[AI模块]")
	private Boolean context;

	/**
	 * 用户类型 UserTypeEnum
	 * 参见 {@link UserTypeEnum}
	 */
	@TableField("user_type")
	private Integer userType;

	@Schema(description = "租户id")
	private Long tenantId;

	/**
	 * 最后上下线时间
	 */
	@TableField(value = "ip_info", typeHandler = JacksonTypeHandler.class)
	private IpInfo ipInfo;
}
