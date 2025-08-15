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
 * @author zuihou
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
     * 微信OpenId
     */
    @TableField(value = "wx_open_id", condition = LIKE)
    private String wxOpenId;

    /**
     * 钉钉OpenId
     */
    @TableField(value = "dd_open_id", condition = LIKE)
    private String ddOpenId;

    /**
     * 内置;[0-否 1-是]
     */
    @TableField(value = "readonly")
    private Boolean readonly;

    /**
     * 性别;
     */
    @TableField(value = "sex")
    private Integer sex;
    /**
     * 民族;[01-汉族 99-其他]	@Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Global.NATION)
     */
    @TableField(value = "nation", condition = LIKE)
    private String nation;

    /**
     * 学历;[01-小学 02-中学 03-高中 04-专科 05-本科 06-硕士 07-博士 08-博士后 99-其他]	@Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Global.EDUCATION)
     */
    @TableField(value = "education", condition = LIKE)
    private String education;

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
                   String wxOpenId, String ddOpenId, Boolean readonly, Integer sex, Boolean state, String workDescribe, String nation,
                   String education,
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
        this.wxOpenId = wxOpenId;
        this.ddOpenId = ddOpenId;
        this.readonly = readonly;
        this.sex = sex;
        this.nation = nation;
        this.education = education;
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
