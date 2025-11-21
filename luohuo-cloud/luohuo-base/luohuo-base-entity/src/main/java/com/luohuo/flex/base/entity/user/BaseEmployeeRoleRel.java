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
 * 员工的角色
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
@TableName("base_employee_role_rel")
@AllArgsConstructor
public class BaseEmployeeRoleRel extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色;#base_role
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 员工;#base_employee
     */
    @TableField(value = "employee_id")
    private Long employeeId;

    /**
     * 创建机构id
     */
    @TableField(value = "created_org_id")
    private Long createdOrgId;


    @Builder
    public BaseEmployeeRoleRel(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime,
                               Long roleId, Long employeeId, Long createdOrgId) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.roleId = roleId;
        this.employeeId = employeeId;
        this.createdOrgId = createdOrgId;
    }

}
