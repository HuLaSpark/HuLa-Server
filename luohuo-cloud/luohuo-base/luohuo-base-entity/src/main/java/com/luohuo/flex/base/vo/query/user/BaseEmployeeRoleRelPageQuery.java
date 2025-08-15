package com.luohuo.flex.base.vo.query.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 员工的角色
 * </p>
 *
 * @author zuihou
 * @since 2021-10-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "员工的角色")
public class BaseEmployeeRoleRelPageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色;#base_role
     */
    @Schema(description = "角色")
    private Long roleId;
    /**
     * 员工;#base_employee
     */
    @Schema(description = "员工")
    private Long employeeId;

}
