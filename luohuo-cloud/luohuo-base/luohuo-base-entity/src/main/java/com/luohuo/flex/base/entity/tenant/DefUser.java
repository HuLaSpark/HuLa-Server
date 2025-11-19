package com.luohuo.flex.base.entity.tenant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.luohuo.basic.base.entity.TenantEntity;
import com.luohuo.flex.model.entity.base.IpInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

import static com.luohuo.flex.model.constant.Condition.LIKE;

/**
 * <p>
 * 实体类
 * 用户总表；
 * 当systemType = 1时, uid关联base服务中的 base_employee的id [主账号登录]
 * 当systemType = 2时, uid关联base服务中的 base_employee的id [RAM登录]
 * 当systemType = 3时, uid关联system服务中的 user的id [IM系统登录]
 * </p>
 *
 * @author 乾乾
 * @since 2021-10-09
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "def_user", autoResultMap = true)
@AllArgsConstructor
public class DefUser extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;

	/**
	 * 系统类型 1-后台登录; 2-IM系统登录
	 */
	@TableField(value = "system_type")
	private Integer systemType;

    /**
     * 用户名;大小写数字下划线
     */
    @TableField(value = "username", condition = LIKE)
    private String username;

    /**
     * 姓名
     */
    @TableField(value = "nick_name", condition = LIKE)
    private String nickName;

	/**
	 * 头像
	 */
	@TableField(value = "avatar", condition = LIKE)
	private String avatar;

    /**
     * 邮箱
     */
    @TableField(value = "email", condition = LIKE)
    private String email;

    /**
     * 手机;1开头11位纯数字
     */
    @TableField(value = "mobile", condition = LIKE)
    private String mobile;

    /**
     * 身份证;15或18位
     */
    @TableField(value = "id_card", condition = LIKE)
    private String idCard;

    /**
     * 性别;
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 状态;[0-禁用 1-启用]
     */
    @TableField(value = "state")
    private Boolean state;

    /**
     * 工作描述
     */
    @TableField(value = "work_describe", condition = LIKE)
    private String workDescribe;

    /**
     * 最后一次输错密码时间
     */
    @TableField(value = "password_error_last_time")
    private LocalDateTime passwordErrorLastTime;

    /**
     * 密码错误次数
     */
    @TableField(value = "password_error_num")
    private Integer passwordErrorNum;

    /**
     * 密码过期时间
     */
    @TableField(value = "password_expire_time")
    private LocalDateTime passwordExpireTime;

    /**
     * 密码
     */
    @TableField(value = "password", condition = LIKE)
    private String password;

    /**
     * 密码盐
     */
    @TableField(value = "salt", condition = LIKE)
    private String salt;

    /**
     * 最后登录时间
     */
    @TableField(value = "last_login_time")
    private LocalDateTime lastLoginTime;

	/**
	 * 最后上下线时间
	 */
	@TableField(value = "ip_info", condition = LIKE, typeHandler = FastjsonTypeHandler.class)
	private IpInfo ipInfo;

	public void refreshIp(String ip) {
		if (ipInfo == null) {
			ipInfo = new IpInfo();
		}
		ipInfo.refreshIp(ip);
	}

    @Builder
    public DefUser(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime,
                   String username, String nickName, String email, String mobile, String idCard,
                   Integer sex, Boolean state, String workDescribe,
                   LocalDateTime passwordErrorLastTime, Integer passwordErrorNum, LocalDateTime passwordExpireTime, String password, String salt, LocalDateTime lastLoginTime) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.username = username;
        this.nickName = nickName;
        this.email = email;
        this.mobile = mobile;
        this.idCard = idCard;
        this.sex = sex;
        this.state = state;
        this.workDescribe = workDescribe;
        this.passwordErrorLastTime = passwordErrorLastTime;
        this.passwordErrorNum = passwordErrorNum;
        this.passwordExpireTime = passwordExpireTime;
        this.password = password;
        this.salt = salt;
        this.lastLoginTime = lastLoginTime;
    }
}
