package com.luohuo.flex.base.manager.application.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.luohuo.basic.base.manager.impl.SuperManagerImpl;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.application.DefApplication;
import com.luohuo.flex.base.entity.application.DefTenantApplicationRel;
import com.luohuo.flex.base.entity.tenant.DefUserTenantRel;
import com.luohuo.flex.base.manager.application.DefApplicationManager;
import com.luohuo.flex.base.manager.application.DefTenantApplicationRelManager;
import com.luohuo.flex.base.manager.tenant.DefUserTenantRelManager;
import com.luohuo.flex.base.mapper.application.DefTenantApplicationRelMapper;
import com.luohuo.flex.common.cache.tenant.application.TenantApplicationCacheKeyBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用管理 | 未来这里可以新增资源与租户的关系
 *
 */
@RequiredArgsConstructor
@Service
public class DefTenantApplicationRelManagerImpl extends SuperManagerImpl<DefTenantApplicationRelMapper, DefTenantApplicationRel> implements DefTenantApplicationRelManager {
    private final DefApplicationManager defApplicationManager;
	private final DefUserTenantRelManager defUserTenantRelManager;
    private final CacheOps cacheOps;

    @Override
    public void grantGeneralApplication(Long tenantId) {
        List<DefApplication> list = defApplicationManager.findGeneral();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<Long> applicationIds = new ArrayList<>();
        List<DefTenantApplicationRel> tarList = list.stream().map(application -> {
            DefTenantApplicationRel tar = new DefTenantApplicationRel();
            tar.setTenantId(tenantId);
            tar.setApplicationId(application.getId());
            applicationIds.add(application.getId());
            return tar;
        }).toList();
        saveBatch(tarList);

//        List<DefResource> resourceList = defResourceManager.findByApplicationId(applicationIds);
//        List<DefTenantResourceRel> trrList = resourceList.stream().map(resource -> {
//            DefTenantResourceRel trr = new DefTenantResourceRel();
//            trr.setTenantId(tenantId);
//            trr.setApplicationId(resource.getApplicationId());
//            trr.setResourceId(resource.getId());
//            return trr;
//        }).toList();
//        defTenantResourceRelManager.saveBatch(trrList);
    }

    @Override
    public List<Long> findApplicationByEmployeeId(Long employeeId) {
        DefUserTenantRel employee = defUserTenantRelManager.getByIdCache(employeeId);
        ArgumentAssert.notNull(employee, "用户不存在");
        Long tenantId = employee.getTenantId();
        CacheKey key = TenantApplicationCacheKeyBuilder.builder(tenantId);
        CacheResult<List<Long>> applicationIds = cacheOps.get(key, k -> listObjs(
                Wraps.<DefTenantApplicationRel>lbQ().select(DefTenantApplicationRel::getApplicationId)
                        .eq(DefTenantApplicationRel::getTenantId, tenantId)
						.and(w ->
								w.gt(DefTenantApplicationRel::getExpirationTime, LocalDateTime.now()).or().isNull(DefTenantApplicationRel::getExpirationTime)
						), Convert::toLong));
        return applicationIds.asList();
    }

    @Override
    public void deleteByTenantId(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        remove(Wraps.<DefTenantApplicationRel>lbQ().in(DefTenantApplicationRel::getTenantId, ids));

        cacheOps.del(ids.stream().map(TenantApplicationCacheKeyBuilder::builder).toList());
    }
}
