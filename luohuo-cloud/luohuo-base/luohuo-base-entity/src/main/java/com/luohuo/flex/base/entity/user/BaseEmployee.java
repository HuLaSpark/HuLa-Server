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

import static com.luohuo.flex.model.constant.Condition.LIKE;

/**
 * <p>
 * 实体类
 * 员工
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
@TableName("base_employee")
@AllArgsConstructor
public class BaseEmployee extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 1 = 主账号
	 * 0 = RAM子账号
     */
    @TableField(value = "user_type")
    private Integer userType;

	/**
	 * 指向主账号（仅对RAM子账号有效）
	 */
	@TableField(value = "parent_id")
	private Long parentId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 上一次登录单位ID
     */
    @TableField(value = "last_company_id")
    private Long lastCompanyId;

    /**
     * 上一次登录部门ID
     */
    @TableField(value = "last_dept_id")
    private Long lastDeptId;

    /**
     * 岗位Id
     */
    @TableField(value = "position_id")
    private Long positionId;

	/**
     * 真实姓名
     */
    @TableField(value = "name", condition = LIKE)
    private String name;

    /**
     * 职位状态;[10-在职 20-离职]@Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Base.POSITION_STATUS)
     */
    @TableField(value = "position_status", condition = LIKE)
    private String positionStatus;
    /**
     * 激活状态;[10-未激活 20-已激活]
     */
    @TableField(value = "active_status", condition = LIKE)
    private String activeStatus;

    /**
     * 状态;[0-禁用 1-启用]
     */
    @TableField(value = "state")
    private Boolean state;

    /**
     * 创建机构Id
     */
    @TableField(value = "created_org_id")
    private Long createdOrgId;


    @Builder
    public BaseEmployee(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime,
                        Integer userType, Long userId, Long positionId, String name, Long lastCompanyId, Long lastDeptId,
                        String positionStatus, String activeStatus, Boolean state, Long createdOrgId) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.userType = userType;
        this.userId = userId;
        this.positionId = positionId;
        this.name = name;
        this.positionStatus = positionStatus;
        this.activeStatus = activeStatus;
        this.state = state;
        this.createdOrgId = createdOrgId;
        this.lastDeptId = lastDeptId;
        this.lastCompanyId = lastCompanyId;
    }

}
