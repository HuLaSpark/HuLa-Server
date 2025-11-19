package com.luohuo.flex.base.service.tenant.impl;

import com.luohuo.basic.base.service.impl.SuperCacheServiceImpl;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.tenant.DefUserTenantRel;
import com.luohuo.flex.base.manager.tenant.DefUserTenantRelManager;
import com.luohuo.flex.base.service.tenant.DefUserTenantRelService;
import com.luohuo.flex.base.vo.query.tenant.DefUserTenantRelResultVO;
import com.luohuo.flex.common.cache.tenant.application.DefUserTenantCacheKeyBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 员工与租户的关系
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefUserTenantRelServiceImpl extends SuperCacheServiceImpl<DefUserTenantRelManager, Long, DefUserTenantRel> implements DefUserTenantRelService {
    @Override
    public List<DefUserTenantRelResultVO> listEmployeeByUserId(Long userId) {
        return superManager.listEmployeeByUserId(userId);
    }

    @Override
    public DefUserTenantRel getEmployeeByTenantAndUser(Long tenantId, Long userId) {
        ArgumentAssert.notNull(tenantId, "租户id为空");
        ArgumentAssert.notNull(userId, "用户id为空");
        return superManager.getOne(Wraps.<DefUserTenantRel>lbQ().eq(DefUserTenantRel::getTenantId, tenantId)
                .eq(DefUserTenantRel::getUserId, userId));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDefaultTenant(Long tenantId, Long userId) {
        ArgumentAssert.notNull(userId, "用户id不能为空");
        ArgumentAssert.notNull(tenantId, "企业id不能为空");

        // 演示环境专用标识
        if (Long.valueOf(1).equals(ContextUtil.getTenantId())) {
            ContextUtil.setStop();
        }

        List<DefUserTenantRel> list = superManager.list(Wraps.<DefUserTenantRel>lbQ().eq(DefUserTenantRel::getUserId, userId));
        CacheKey[] keyList = list.stream().map(item -> DefUserTenantCacheKeyBuilder.build(item.getId())).toArray(CacheKey[]::new);
        cacheOps.del(keyList);

        superManager.update(Wraps.<DefUserTenantRel>lbU()
                .set(DefUserTenantRel::getIsDefault, false)
                .eq(DefUserTenantRel::getUserId, userId));

        return superManager.update(Wraps.<DefUserTenantRel>lbU()
                .set(DefUserTenantRel::getIsDefault, true)
                .eq(DefUserTenantRel::getUserId, userId)
                .eq(DefUserTenantRel::getTenantId, tenantId)
        );
    }
}
