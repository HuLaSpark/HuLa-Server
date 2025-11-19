package com.luohuo.flex.base.service.tenant.impl;

import com.luohuo.basic.base.service.impl.SuperCacheServiceImpl;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.BeanPlusUtil;
import com.luohuo.flex.base.entity.tenant.DefTenant;
import com.luohuo.flex.base.enumeration.tenant.DefTenantRegisterTypeEnum;
import com.luohuo.flex.base.manager.application.DefTenantApplicationRelManager;
import com.luohuo.flex.base.manager.tenant.DefTenantManager;
import com.luohuo.flex.base.service.tenant.DefTenantService;
import com.luohuo.flex.base.vo.result.user.DefTenantResultVO;
import com.luohuo.flex.base.vo.save.tenant.DefTenantSaveVO;
import com.luohuo.flex.common.constant.AppendixType;
import com.luohuo.flex.file.service.AppendixService;
import com.luohuo.flex.model.enumeration.system.DefTenantStatusEnum;
import com.luohuo.flex.model.vo.save.AppendixSaveVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 企业
 * </p>
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefTenantServiceImpl extends SuperCacheServiceImpl<DefTenantManager, Long, DefTenant> implements DefTenantService {
    private final AppendixService appendixService;
    private final DefTenantApplicationRelManager defTenantApplicationRelManager;

    @Override
    public boolean check(String name) {
        return superManager.count(Wraps.<DefTenant>lbQ().eq(DefTenant::getName, name)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DefTenant register(DefTenantSaveVO defTenantSaveVO) {
        // 1， 保存租户 (默认库)
        DefTenant tenant = BeanPlusUtil.toBean(defTenantSaveVO, DefTenant.class);
        tenant.setStatus(DefTenantStatusEnum.WAITING.getCode());
        tenant.setRegisterType(DefTenantRegisterTypeEnum.REGISTER);

        superManager.save(tenant);
        appendixService.save(AppendixSaveVO.build(tenant.getId(), AppendixType.System.DEF__TENANT__LOGO, defTenantSaveVO.getLogo()));
        return tenant;
    }

    @Override
    protected <SaveVO> DefTenant saveBefore(SaveVO saveVO) {
        DefTenantSaveVO defTenantSaveVO = (DefTenantSaveVO) saveVO;
        // 1， 保存租户 (默认库)
        DefTenant tenant = BeanPlusUtil.toBean(defTenantSaveVO, DefTenant.class);
        tenant.setStatus(DefTenantStatusEnum.WAITING.getCodeInt());
        tenant.setRegisterType(DefTenantRegisterTypeEnum.CREATE);
        return tenant;
    }

    @Override
    protected <SaveVO> void saveAfter(SaveVO saveVO, DefTenant defTenant) {
        DefTenantSaveVO defTenantSaveVO = (DefTenantSaveVO) saveVO;
        appendixService.save(AppendixSaveVO.build(defTenant.getId(), AppendixType.System.DEF__TENANT__LOGO, defTenantSaveVO.getLogo()));

    }

//    private Boolean updateTenantStatus(DefTenantInitVO initVO) {
//        Boolean flag = superManager.update(Wraps.<DefTenant>lbU().set(DefTenant::getStatus, DefTenantStatusEnum.WAIT_INIT_DATASOURCE.getCode())
//                .set(DefTenant::getConnectType, initVO.getConnectType())
//                .eq(DefTenant::getId, initVO.getId()));
//        superManager.delCache(initVO.getId());
//        defTenantApplicationRelManager.grantGeneralApplication(initVO.getId());
//        return flag;
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(List<Long> ids) {
        appendixService.removeByBizId(ids, AppendixType.System.DEF__TENANT__LOGO);
//        defTenantDatasourceConfigRelManager.remove(Wraps.<DefTenantDatasourceConfigRel>lbQ().in(DefTenantDatasourceConfigRel::getTenantId, ids));
        defTenantApplicationRelManager.deleteByTenantId(ids);
//        defTenantResourceRelManager.deleteByTenantId(ids);
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAll(List<Long> ids) {
        appendixService.removeByBizId(ids, AppendixType.System.DEF__TENANT__LOGO);
        removeByIds(ids);
//        defTenantDatasourceConfigRelManager.remove(Wraps.<DefTenantDatasourceConfigRel>lbQ().in(DefTenantDatasourceConfigRel::getTenantId, ids));
        defTenantApplicationRelManager.deleteByTenantId(ids);
//        defTenantResourceRelManager.deleteByTenantId(ids);
//        return initSystemContext.delete(ids);
		return true;
    }

    @Override
    public List<DefTenant> findNormalTenant() {
        return list(Wraps.<DefTenant>lbQ().eq(DefTenant::getStatus, DefTenantStatusEnum.NORMAL));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(Long id, Integer status, String reviewComments) {
        ArgumentAssert.notNull(id, "请传递正确的企业ID");
        ArgumentAssert.notNull(status, "请传递正确的状态值");

        if (DefTenantStatusEnum.NORMAL.eq(status)) {
			// 审核通过后，进行初始化
//            DefTenantInitVO tenantConnect = new DefTenantInitVO();
//            tenantConnect.setConnectType(TenantConnectTypeEnum.SYSTEM);
//            tenantConnect.setId(id);
//            initSystemContext.initData(tenantConnect);

            defTenantApplicationRelManager.grantGeneralApplication(id);
            status = DefTenantStatusEnum.WAIT_INIT_DATASOURCE.getCode();
        }

        boolean update = superManager.update(Wraps.<DefTenant>lbU().set(DefTenant::getStatus, status).eq(DefTenant::getId, id));
        superManager.delCache(id);
        return update;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(Long id, Boolean state) {
        ArgumentAssert.notNull(id, "请选择正确的企业进行修改");
        ArgumentAssert.notNull(state, "请传递正确的状态值");
        // 演示环境专用标识，用于WriteInterceptor拦截器判断演示环境需要禁止用户执行sql，若您无需搭建演示环境，可以删除下面一行代码
        ContextUtil.setStop();
        boolean update = superManager.update(Wraps.<DefTenant>lbU().set(DefTenant::getState, state).eq(DefTenant::getId, id));
        superManager.delCache(id);
        return update;
    }

    @Override
    public List<DefTenantResultVO> listTenantByUserId(Long userId) {
        return superManager.listTenantByUserId(userId);
    }
}
