package com.luohuo.flex.base.service.system;

import com.luohuo.basic.base.service.SuperCacheService;
import com.luohuo.flex.base.entity.system.BaseRole;
import com.luohuo.flex.base.vo.save.system.BaseRoleResourceRelSaveVO;
import com.luohuo.flex.base.vo.save.system.RoleEmployeeSaveVO;
import com.luohuo.flex.model.enumeration.base.RoleCategoryEnum;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 角色
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
public interface BaseRoleService extends SuperCacheService<Long, BaseRole> {
    /**
     * 检测角色编码是否存在
     *
     * @param code 角色编码
     * @param id   角色id
     * @return
     */
    Boolean check(String code, Long id);

    /**
     * 保存角色
     *
     * @param roleEmployeeSaveVO
     * @return
     */
    List<Long> saveRoleEmployee(RoleEmployeeSaveVO roleEmployeeSaveVO);


    /**
     * 根据角色id查询角色下的员工id
     *
     * @param roleId
     * @return
     */
    List<Long> findEmployeeIdByRoleId(Long roleId);

    /**
     * 根据角色id查询 角色下的资源id
     *
     * @param roleId   角色ID
     * @param category 角色类别
     * @return
     */
    Map<Long, Collection<Long>> findResourceIdByRoleId(Long roleId, RoleCategoryEnum category);

    /**
     * 保存角色下的资源
     *
     * @param saveVO
     * @return
     */
    Boolean saveRoleResource(BaseRoleResourceRelSaveVO saveVO);

    /**
     * 查询员工拥有的资源
     *
     * @param employeeId    员工ID
     * @param applicationId 应用ID
     * @return java.util.List<java.lang.Long>
     * @author tangyh
     * @date 2022/10/20 5:26 PM
     * @create [2022/10/20 5:26 PM ] [tangyh] [初始创建]
     */
    List<Long> findResourceIdByEmployeeId(Long applicationId, Long employeeId);

    /**
     * 检查某个员工{employeeId}是否拥有任意一个角色{codes}
     *
     * @param employeeId employeeId
     * @param codes      codes
     * @return boolean
     * @author tangyh
     * @date 2022/4/24 2:49 PM
     * @create [2022/4/24 2:49 PM ] [tangyh] [初始创建]
     */
    boolean checkRole(Long employeeId, String... codes);

    /**
     * 查询员工拥有的角色编码
     *
     * @param employeeId 员工ID
     * @return java.util.List<java.lang.String>
     * @author tangyh
     * @date 2022/10/27 3:17 PM
     * @create [2022/10/27 3:17 PM ] [tangyh] [初始创建]
     */
    List<String> findRoleCodeByEmployeeId(Long employeeId);
}
