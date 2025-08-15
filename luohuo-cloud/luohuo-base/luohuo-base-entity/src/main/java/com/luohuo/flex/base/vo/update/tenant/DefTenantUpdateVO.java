package com.luohuo.flex.base.vo.update.tenant;

import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(title = "DefTenantUpdateVO", description = "企业")
public class DefTenantUpdateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @NotNull(message = "请填写主键", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 企业名称
     */
    @Schema(description = "企业名称")
    @NotEmpty(message = "请填写企业名称")
    @Size(max = 255, message = "企业名称长度不能超过{max}")
    private String name;
    /**
     * 企业简称
     */
    @Schema(description = "企业简称")
    @Size(max = 255, message = "企业简称长度不能超过{max}")
    private String abbreviation;
    /**
     * 统一社会信用代码
     */
    @Schema(description = "统一社会信用代码")
    @Size(max = 18, message = "统一社会信用代码长度不能超过{max}")
    private String creditCode;
    /**
     * 联系人
     */
    @Schema(description = "联系人")
    @Size(max = 255, message = "联系人长度不能超过{max}")
    private String contactPerson;
    /**
     * 联系方式
     */
    @Schema(description = "联系方式")
    @Size(max = 255, message = "联系方式长度不能超过{max}")
    private String contactPhone;
    /**
     * 联系邮箱
     */
    @Schema(description = "联系邮箱")
    @Size(max = 255, message = "联系邮箱长度不能超过{max}")
    private String contactEmail;
    /** 类别 */
    @Schema(description = "类别")
    @Size(max = 255, message = "类别长度不能超过{max}")
    private String classify;
    /**
     * 省
     */
    @Schema(description = "省")
    private Long provinceId;
    /**
     * 省
     */
    @Schema(description = "省")
    @Size(max = 255, message = "省长度不能超过{max}")
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
    @Size(max = 255, message = "市长度不能超过{max}")
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
    @Size(max = 255, message = "区长度不能超过{max}")
    private String districtName;
    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    @Size(max = 255, message = "详细地址长度不能超过{max}")
    private String address;

    /**
     * 状态;0-禁用 1-启用
     */
    @Schema(description = "状态")
    private Boolean state;

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
    @Size(max = 255, message = "企业简介长度不能超过{max}")
    private String describe;
    /**
     * logo
     */
    @Schema(description = "企业logo")
    private Long logo;
}
