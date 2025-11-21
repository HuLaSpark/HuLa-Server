package com.luohuo.flex.base.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.TenantEntity;
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
 * 角色的资源
 * </p>
 *
 * @author 乾乾
 * @since 2021-10-21
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("base_role_resource_rel")
@AllArgsConstructor
public class BaseRoleResourceRel extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 资源id;#def_resource
     */
    @TableField(value = "resource_id")
    private Long resourceId;

    /**
     * 应用id;#def_application
     */
    @TableField(value = "application_id")
    private Long applicationId;

    /**
     * 角色id;#base_role
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 组织ID
     */
    @TableField(value = "created_org_id")
    private Long createdOrgId;


    @Builder
    public BaseRoleResourceRel(Long id, LocalDateTime createdTime, Long createdBy, LocalDateTime updatedTime, Long updatedBy,
                               Long resourceId, Long applicationId, Long roleId, Long createdOrgId) {
        this.id = id;
        this.createTime = createdTime;
        this.createBy = createdBy;
        this.updateTime = updatedTime;
        this.updateBy = updatedBy;
        this.resourceId = resourceId;
        this.applicationId = applicationId;
        this.roleId = roleId;
        this.createdOrgId = createdOrgId;
    }

}
