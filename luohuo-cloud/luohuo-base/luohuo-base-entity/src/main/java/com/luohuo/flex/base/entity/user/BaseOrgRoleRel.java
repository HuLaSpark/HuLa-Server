package com.luohuo.flex.base.entity.user;

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
 * 组织的角色
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
@TableName("base_org_role_rel")
@AllArgsConstructor
public class BaseOrgRoleRel extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 部门;#base_org
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 角色;#base_role
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 创建机构ID
     */
    @TableField(value = "created_org_id")
    private Long createdOrgId;


    @Builder
    public BaseOrgRoleRel(Long id, LocalDateTime createdTime, Long createdBy, LocalDateTime updatedTime, Long updatedBy,
                          Long orgId, Long roleId, Long createdOrgId) {
        this.id = id;
        this.createTime = createdTime;
        this.createBy = createdBy;
        this.updateTime = updatedTime;
        this.updateBy = updatedBy;
        this.orgId = orgId;
        this.roleId = roleId;
        this.createdOrgId = createdOrgId;
    }

}
