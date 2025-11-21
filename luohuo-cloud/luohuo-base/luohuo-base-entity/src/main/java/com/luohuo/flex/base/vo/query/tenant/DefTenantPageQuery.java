package com.luohuo.flex.base.vo.query.tenant;

import com.luohuo.flex.base.enumeration.tenant.DefTenantRegisterTypeEnum;
import com.luohuo.flex.model.enumeration.system.TenantConnectTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 企业
 * </p>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(title = "DefTenantPageQuery", description = "企业")
public class DefTenantPageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 企业编码
     */
    @Schema(description = "企业编码")
    private String code;
    /**
     * 企业名称
     */
    @Schema(description = "企业名称")
    private String name;
    /**
     * 企业简称
     */
    @Schema(description = "企业简称")
    private String abbreviation;
    /**
     * 统一社会信用代码
     */
    @Schema(description = "统一社会信用代码")
    private String creditCode;

    /**
     * 联系人
     */
    @Schema(description = "联系人")
    private String contactPerson;
    /** 类别 */
    @Schema(description = "类别")
    private String classify;

    /**
     * 联系方式
     */
    @Schema(description = "联系方式")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Schema(description = "联系邮箱")
    private String contactEmail;

    /**
     * 省
     */
    @Schema(description = "省")
    private Long provinceId;

    /**
     * 省
     */
    @Schema(description = "省")
    private String provinceName;

    /**
     * 市
     */
    @Schema(description = "市")
    private Long cityId;

    /**
     * 市
     */
    @Schema(description = "市")
    private String cityName;

    /**
     * 区
     */
    @Schema(description = "区")
    private Long districtId;

    /**
     * 区
     */
    @Schema(description = "区")
    private String districtName;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;
    /**
     * 类型;
     * #{CREATE:创建;REGISTER:注册}
     */
    @Schema(description = "类型")
    private DefTenantRegisterTypeEnum registerType;
    /**
     * 数据源链接类型;#TenantConnectTypeEnum{LOCAL:本地;REMOTE:远程}
     */
    @Schema(description = "数据源链接类型")
    private TenantConnectTypeEnum connectType;
    /**
     * 状态;
     * #{NORMAL:正常;WAITING:审核中;REFUSE:停用;WAIT_INIT_DATASOURCE:待初始化租户}
     */
    @Schema(description = "审核状态")
    private Integer status;
    /**
     * 状态;0-禁用 1-启用
     */
    @Schema(description = "状态")
    private Boolean state;
    /**
     * 内置
     */
    @Schema(description = "内置")
    private Boolean readonly;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createdName;
    /**
     * 有效期;
     * 为空表示永久
     */
    @Schema(description = "有效期")
    private LocalDateTime expirationTime;
    /**
     * 企业简介
     */
    @Schema(description = "企业简介")
    private String describe;

}
