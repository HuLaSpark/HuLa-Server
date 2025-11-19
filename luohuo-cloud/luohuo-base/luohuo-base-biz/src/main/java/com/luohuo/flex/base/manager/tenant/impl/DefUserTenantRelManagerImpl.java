package com.luohuo.flex.base.manager.tenant.impl;

import com.luohuo.basic.base.manager.impl.SuperCacheManagerImpl;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.base.entity.tenant.DefUserTenantRel;
import com.luohuo.flex.base.manager.tenant.DefUserTenantRelManager;
import com.luohuo.flex.base.mapper.application.DefUserTenantRelMapper;
import com.luohuo.flex.base.vo.query.tenant.DefUserTenantRelResultVO;
import com.luohuo.flex.common.cache.tenant.application.DefUserTenantCacheKeyBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * baseEmployee与租户的关系
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefUserTenantRelManagerImpl extends SuperCacheManagerImpl<DefUserTenantRelMapper, DefUserTenantRel> implements DefUserTenantRelManager {
    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new DefUserTenantCacheKeyBuilder();
    }


    @Override
    public List<DefUserTenantRelResultVO> listEmployeeByUserId(Long userId) {
        return baseMapper.listEmployeeByUserId(userId);
    }
}
