package com.luohuo.flex.base.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.base.service.impl.SuperCacheServiceImpl;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.database.mybatis.conditions.query.LbQueryWrap;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.entity.user.BaseEmployeeOrgRel;
import com.luohuo.flex.base.entity.user.BaseEmployeeRoleRel;
import com.luohuo.flex.base.manager.user.BaseEmployeeManager;
import com.luohuo.flex.base.manager.user.BaseEmployeeOrgRelManager;
import com.luohuo.flex.base.manager.user.BaseEmployeeRoleRelManager;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.vo.query.user.BaseEmployeePageQuery;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeRoleRelSaveVO;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeSaveVO;
import com.luohuo.flex.base.vo.update.user.BaseEmployeeUpdateVO;
import com.luohuo.flex.common.cache.base.user.EmployeeOrgCacheKeyBuilder;
import com.luohuo.flex.common.cache.base.user.EmployeeRoleCacheKeyBuilder;

import com.luohuo.flex.common.constant.RoleConstant;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 员工
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class BaseEmployeeServiceImpl extends SuperCacheServiceImpl<BaseEmployeeManager, Long, BaseEmployee> implements BaseEmployeeService {
    private final BaseEmployeeRoleRelManager baseEmployeeRoleRelManager;
    private final BaseEmployeeOrgRelManager baseEmployeeOrgRelManager;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrgInfo(Long id, Long lastCompanyId, Long lastDeptId) {
        superManager.update(Wrappers.<BaseEmployee>lambdaUpdate().set(BaseEmployee::getLastCompanyId, lastCompanyId)
                .set(BaseEmployee::getLastDeptId, lastDeptId).eq(BaseEmployee::getId, id));
    }

	@Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<BaseEmployee> entityList) {
        return superManager.saveBatch(entityList);
    }

    @Override
    public IPage<BaseEmployeeResultVO> findPageResultVO(PageParams<BaseEmployeePageQuery> params) {
        IPage<BaseEmployee> page = params.buildPage(BaseEmployee.class);
        BaseEmployeePageQuery model = params.getModel();
        LbQueryWrap<BaseEmployee> wrap = Wraps.lbQ();
        wrap.like(BaseEmployee::getName, model.getName())
                .eq(BaseEmployee::getPositionStatus, model.getPositionStatus())
                .eq(BaseEmployee::getPositionId, model.getPositionId())
                .eq(BaseEmployee::getActiveStatus, model.getActiveStatus())
                .eq(BaseEmployee::getState, model.getState())
                .in(BaseEmployee::getUserId, model.getUserIdList());

        return superManager.selectPageResultVO(page, wrap, model);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> saveEmployeeRole(BaseEmployeeRoleRelSaveVO saveVO) {
        if (saveVO.getFlag() == null) {
            saveVO.setFlag(true);
        }

        baseEmployeeRoleRelManager.remove(Wraps.<BaseEmployeeRoleRel>lbQ().eq(BaseEmployeeRoleRel::getEmployeeId, saveVO.getEmployeeId())
                .in(BaseEmployeeRoleRel::getRoleId, saveVO.getRoleIdList()));

        if (saveVO.getFlag() && CollUtil.isNotEmpty(saveVO.getRoleIdList())) {
            List<BaseEmployeeRoleRel> list = saveVO.getRoleIdList().stream()
                    .map(roleId -> BaseEmployeeRoleRel.builder()
                            .roleId(roleId).employeeId(saveVO.getEmployeeId())
                            .build()).toList();
            baseEmployeeRoleRelManager.saveBatch(list);
        }

        cacheOps.del(EmployeeRoleCacheKeyBuilder.build(saveVO.getEmployeeId()));
        return findEmployeeRoleByEmployeeId(saveVO.getEmployeeId());
    }

    @Override
    public List<Long> findEmployeeRoleByEmployeeId(Long employeeId) {
        return baseEmployeeRoleRelManager.listObjs(Wrappers.<BaseEmployeeRoleRel>lambdaQuery()
                        .select(BaseEmployeeRoleRel::getRoleId)
                        .eq(BaseEmployeeRoleRel::getEmployeeId, employeeId),
                Convert::toLong
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <SaveVO> BaseEmployee save(SaveVO saveVO) {
        BaseEmployeeSaveVO employeeSaveVO = (BaseEmployeeSaveVO) saveVO;
        BaseEmployee baseEmployee = BeanUtil.toBean(employeeSaveVO, BaseEmployee.class);
        baseEmployee.setCreatedOrgId(ContextUtil.getCurrentDeptId());
        superManager.save(baseEmployee);
        List<Long> orgIdList = employeeSaveVO.getOrgIdList();
        saveEmployeeOrg(baseEmployee, orgIdList);
        return baseEmployee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <UpdateVO> BaseEmployee updateById(UpdateVO updateVO) {
        BaseEmployeeUpdateVO employeeUpdateVO = (BaseEmployeeUpdateVO) updateVO;
        BaseEmployee baseEmployee = BeanUtil.toBean(updateVO, BaseEmployee.class);
        superManager.updateById(baseEmployee);
        List<Long> orgIdList = employeeUpdateVO.getOrgIdList();

        saveEmployeeOrg(baseEmployee, orgIdList);
        return baseEmployee;
    }

    private void saveEmployeeOrg(BaseEmployee baseEmployee, List<Long> orgIdList) {
        baseEmployeeOrgRelManager.removeByEmployeeId(baseEmployee.getId());
        if (CollUtil.isNotEmpty(orgIdList)) {
            List<BaseEmployeeOrgRel> eoList = orgIdList.stream().map(orgId ->
                    BaseEmployeeOrgRel.builder()
                            .employeeId(baseEmployee.getId())
                            .orgId(orgId)
                            .build()).toList();
            baseEmployeeOrgRelManager.saveBatch(eoList);
        }

        cacheOps.del(EmployeeOrgCacheKeyBuilder.build(baseEmployee.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<Long> idList) {
        boolean flag = superManager.removeByIds(idList);
        baseEmployeeOrgRelManager.removeByEmployeeIds(idList);
        baseEmployeeRoleRelManager.removeByEmployeeIds(idList);
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatchBaseEmployeeAndRole(List<BaseEmployee> employeeList) {
        ArgumentAssert.notEmpty(employeeList, "员工列表不能为空");
        superManager.saveBatch(employeeList);

        List<Long> employeeIdList = employeeList.stream().map(BaseEmployee::getId).toList();
        return baseEmployeeRoleRelManager.bindRole(employeeIdList, RoleConstant.TENANT_ADMIN);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(BaseEmployee baseEmployee) {
        return superManager.updateById(baseEmployee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllById(BaseEmployee baseEmployee) {
        return superManager.updateAllById(baseEmployee);
    }

    @Override
    public BaseEmployee getEmployeeByUser(Long userId) {
        return superManager.getEmployeeByUser(userId);
    }

    @Override
    public List<BaseEmployeeResultVO> listEmployeeByUserId(Long userId) {
        return superManager.listEmployeeByUserId(userId);
    }
}
