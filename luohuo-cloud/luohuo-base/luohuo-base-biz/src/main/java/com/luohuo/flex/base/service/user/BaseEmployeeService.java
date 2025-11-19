package com.luohuo.flex.base.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.base.service.SuperCacheService;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.vo.query.user.BaseEmployeePageQuery;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeRoleRelSaveVO;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 员工
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
public interface BaseEmployeeService extends SuperCacheService<Long, BaseEmployee> {
    /**
     * 批量保存
     *
     * @param entityList entityList
     * @return boolean
     * @author tangyh
     * @date 2022/10/28 4:38 PM
     * @create [2022/10/28 4:38 PM ] [tangyh] [初始创建]
     */
    boolean saveBatch(Collection<BaseEmployee> entityList);

    /**
     * 给员工分配角色
     *
     * @param employeeRoleSaveVO
     * @return
     */
    List<Long> saveEmployeeRole(BaseEmployeeRoleRelSaveVO employeeRoleSaveVO);

    /**
     * 根据员工id查询员工的角色
     *
     * @param employeeId 员工id
     * @return
     */
    List<Long> findEmployeeRoleByEmployeeId(Long employeeId);

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    IPage<BaseEmployeeResultVO> findPageResultVO(PageParams<BaseEmployeePageQuery> params);


    /**
     * 批量保存 基础库员工和系统角色
     *
     * @param employeeList
     * @return
     */
    boolean saveBatchBaseEmployeeAndRole(List<BaseEmployee> employeeList);

    /**
     * 根据ID修改不为空的字段
     *
     * @param baseEmployee baseEmployee
     * @return boolean
     * @author tangyh
     * @date 2022/10/28 9:20 AM
     * @create [2022/10/28 9:20 AM ] [tangyh] [初始创建]
     */
    boolean updateById(BaseEmployee baseEmployee);

    /**
     * 根据ID修改所有的字段
     *
     * @param baseEmployee baseEmployee
     * @return boolean
     * @author tangyh
     * @date 2022/10/28 9:20 AM
     * @create [2022/10/28 9:20 AM ] [tangyh] [初始创建]
     */
    boolean updateAllById(BaseEmployee baseEmployee);

    /**
     * 查询指定企业指定用户的员工信息
     *
     * @param userId 用户id
     * @return
     */
    BaseEmployee getEmployeeByUser(Long userId);


    /**
     * 根据用户id查询员工
     *
     * @param userId 用户id
     * @return
     */
    List<BaseEmployeeResultVO> listEmployeeByUserId(Long userId);

    /**
     * 修改员工的上次登录单位和部门id
     * @param id 员工id
     * @param lastCompanyId 上次登录单位id
     * @param lastDeptId 上次登录部门id
     */
    void updateOrgInfo(Long id, Long lastCompanyId, Long lastDeptId);
}
