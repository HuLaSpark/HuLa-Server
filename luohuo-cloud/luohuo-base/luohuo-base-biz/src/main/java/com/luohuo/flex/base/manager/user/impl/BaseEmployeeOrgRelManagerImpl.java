package com.luohuo.flex.base.manager.user.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.impl.SuperManagerImpl;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.database.mybatis.conditions.query.LbQueryWrap;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.user.BaseEmployeeOrgRel;
import com.luohuo.flex.base.manager.user.BaseEmployeeOrgRelManager;
import com.luohuo.flex.base.mapper.user.BaseEmployeeOrgRelMapper;
import com.luohuo.flex.base.mapper.user.BaseOrgMapper;
import com.luohuo.flex.common.cache.base.user.EmployeeOrgCacheKeyBuilder;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 通用业务实现类
 * 员工所在部门
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 * @create [2021-10-18] [zuihou] [代码生成器生成]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseEmployeeOrgRelManagerImpl extends SuperManagerImpl<BaseEmployeeOrgRelMapper, BaseEmployeeOrgRel> implements BaseEmployeeOrgRelManager {
    private final CacheOps cacheOps;
    private final BaseOrgMapper orgMapper;

    @Override
    public List<Long> findOrgIdByEmployeeId(Long employeeId) {
        CacheKey eoKey = EmployeeOrgCacheKeyBuilder.build(employeeId);
        CacheResult<List<Long>> orgIdResult = cacheOps.get(eoKey, k -> orgMapper.selectOrgByEmployeeId(employeeId));
        return orgIdResult.asList();
    }

    @Override
    public boolean removeByEmployeeIds(Collection<Long> employeeIds) {
        ArgumentAssert.notEmpty(employeeIds, "员工ID不能为空");

        boolean remove = remove(Wraps.<BaseEmployeeOrgRel>lbQ().in(BaseEmployeeOrgRel::getEmployeeId, employeeIds));
        cacheOps.del(employeeIds.stream().map(EmployeeOrgCacheKeyBuilder::build).toList());
        return remove;
    }

    @Override
    public void deleteByOrg(Collection<Long> orgIdList) {
        if (CollUtil.isEmpty(orgIdList)) {
            return;
        }
        LbQueryWrap<BaseEmployeeOrgRel> wrap = Wraps.<BaseEmployeeOrgRel>lbQ().in(BaseEmployeeOrgRel::getOrgId, orgIdList);
        List<BaseEmployeeOrgRel> list = list(wrap);
        remove(wrap);
        List<CacheKey> keys = list.stream()
                .map(BaseEmployeeOrgRel::getEmployeeId)
                .distinct()
                .map(EmployeeOrgCacheKeyBuilder::build)
                .toList();
        cacheOps.del(keys);
    }
}
