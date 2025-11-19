package com.luohuo.flex.base.entity.tenant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import com.luohuo.flex.base.enumeration.tenant.DefTenantRegisterTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * <p>
 * 租户企业
 * </p>
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("def_tenant")
@AllArgsConstructor
public class DefTenant extends Entity<Long> {

	private static final long serialVersionUID = 1L;

	@Schema(description = "租户名 | 企业名称")
	private String name;

	@Schema(description = "联系人的用户编号")
	private Long contactUserId;

	@Schema(description = "联系人")
	private String contactName;

	@Schema(description = "联系手机")
	private String contactMobile;

	@Schema(description = "租户状态（0正常 1审核中 2停用）")
	private Integer status;

	/**
	 * 状态;false-禁用 true-启用
	 */
	@Schema(description = "状态（正式租户、非正式租户的停用启用状态）")
	private Boolean state;

	@Schema(description = "绑定域名")
	private String website;

	@Schema(description = "类型;#{CREATE:创建;REGISTER:注册}")
	private DefTenantRegisterTypeEnum registerType;

	@Schema(description = "租户套餐编号")
	private Long packageId;

	@Schema(description = "过期时间")
	private LocalDateTime expireTime;

	@Schema(description = "账号数量")
	private Integer accountCount;
}
