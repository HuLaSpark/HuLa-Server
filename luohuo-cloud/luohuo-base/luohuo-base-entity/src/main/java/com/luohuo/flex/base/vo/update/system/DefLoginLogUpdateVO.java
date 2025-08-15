package com.luohuo.flex.base.vo.update.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.base.entity.SuperEntity;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 登录日志
 * </p>
 *
 * @author zuihou
 * @since 2021-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "登录日志")
public class DefLoginLogUpdateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @NotNull(message = "请填写主键", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 登录员工
     */
    @Schema(description = "登录员工")
    private Long employeeId;
    /**
     * 登录用户
     */
    @Schema(description = "登录用户")
    private Long userId;
    /**
     * 登录IP
     */
    @Schema(description = "登录IP")
    @Size(max = 50, message = "登录IP长度不能超过{max}")
    private String requestIp;
    /**
     * 登录人姓名
     */
    @Schema(description = "登录人姓名")
    @Size(max = 50, message = "登录人姓名长度不能超过{max}")
    private String nickName;
    /**
     * 登录人账号
     */
    @Schema(description = "登录人账号")
    @Size(max = 30, message = "登录人账号长度不能超过{max}")
    private String username;
    /**
     * 登录描述
     */
    @Schema(description = "登录描述")
    @Size(max = 255, message = "登录描述长度不能超过{max}")
    private String description;
    /**
     * 登录时间
     */
    @Schema(description = "登录时间")
    @Size(max = 10, message = "登录时间长度不能超过{max}")
    private String loginDate;
    /**
     * 浏览器请求头
     */
    @Schema(description = "浏览器请求头")
    @Size(max = 500, message = "浏览器请求头长度不能超过{max}")
    private String ua;
    /**
     * 浏览器名称
     */
    @Schema(description = "浏览器名称")
    @Size(max = 255, message = "浏览器名称长度不能超过{max}")
    private String browser;
    /**
     * 浏览器版本
     */
    @Schema(description = "浏览器版本")
    @Size(max = 255, message = "浏览器版本长度不能超过{max}")
    private String browserVersion;
    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    @Size(max = 255, message = "操作系统长度不能超过{max}")
    private String operatingSystem;
    /**
     * 登录地点
     */
    @Schema(description = "登录地点")
    @Size(max = 50, message = "登录地点长度不能超过{max}")
    private String location;
}
