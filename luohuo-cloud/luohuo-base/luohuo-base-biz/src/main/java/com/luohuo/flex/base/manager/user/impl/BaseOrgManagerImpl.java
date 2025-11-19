package com.luohuo.flex.base.manager.user.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.manager.impl.SuperCacheManagerImpl;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.flex.base.entity.user.BaseOrg;
import com.luohuo.flex.base.manager.user.BaseOrgManager;
import com.luohuo.flex.base.mapper.user.BaseOrgMapper;
import com.luohuo.flex.common.cache.base.user.OrgCacheKeyBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * 通用业务实现类
 * 组织
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 * @create [2021-10-18] [zuihou] [代码生成器生成]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseOrgManagerImpl extends SuperCacheManagerImpl<BaseOrgMapper, BaseOrg> implements BaseOrgManager {

    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new OrgCacheKeyBuilder();
    }

    @Override
    @Transactional(readOnly = true)
    
    public Map<Serializable, Object> findByIds(Set<Serializable> params) {
        if (CollUtil.isEmpty(params)) {
            return Collections.emptyMap();
        }
        Set<Serializable> ids = new HashSet<>();
        params.forEach(item -> {
            if (item instanceof Collection tempItem) {
                ids.addAll(tempItem);
            } else {
                ids.add(item);
            }
        });

        List<BaseOrg> list = findByIds(ids, null);

        return CollHelper.uniqueIndex(list.stream().filter(Objects::nonNull).toList(), BaseOrg::getId, BaseOrg::getName);
    }

}
