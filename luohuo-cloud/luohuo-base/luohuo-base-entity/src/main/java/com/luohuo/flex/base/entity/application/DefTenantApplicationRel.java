package com.luohuo.flex.base.entity.application;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.annotation.echo.Echo;
import com.luohuo.basic.base.entity.Entity;
import com.luohuo.flex.model.constant.EchoApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 租户的应用
 * </p>
 *
 * @author 乾乾
 * @since 2021-09-26
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("def_tenant_application_rel")
@AllArgsConstructor
public class DefTenantApplicationRel extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    @Echo(api = EchoApi.DEF_TENANT_SERVICE_IMPL_CLASS)
    private Long tenantId;

    /**
     * 应用ID
     */
    @TableField(value = "application_id")
    @Echo(api = EchoApi.DEF_APPLICATION_SERVICE_IMPL_CLASS)
    private Long applicationId;

    /**
     * 过期时间
     */
    @TableField(value = "expiration_time")
    private LocalDateTime expirationTime;

    @Builder
    public DefTenantApplicationRel(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime, Long tenantId, Long applicationId, LocalDateTime expirationTime) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.tenantId = tenantId;
        this.applicationId = applicationId;
        this.expirationTime = expirationTime;
    }

}
