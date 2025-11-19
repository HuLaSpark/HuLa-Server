package com.luohuo.flex.base.entity.tenant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
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
 * 员工
 * </p>
 *
 * @author 乾乾
 * @since 2021-10-27
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("def_user_tenant_rel")
@AllArgsConstructor
public class DefUserTenantRel extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否默认员工;[0-否 1-是]
     */
    @TableField(value = "is_default")
    private Boolean isDefault;

    /**
     * 用户
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 状态;[0-禁用 1-启用]
     */
    @TableField(value = "state")
    private Boolean state;

    /**
     * 所属企业
     */
    @TableField(value = "tenant_id")
    private Long tenantId;


    @Builder
    public DefUserTenantRel(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime,
                            Boolean isDefault, Long userId, Boolean state, Long tenantId) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.isDefault = isDefault;
        this.userId = userId;
        this.state = state;
        this.tenantId = tenantId;
    }

}
