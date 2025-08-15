package com.luohuo.flex.base.manager.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.impl.SuperCacheManagerImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.flex.base.entity.application.DefApplication;
import com.luohuo.flex.base.mapper.application.DefApplicationMapper;
import com.luohuo.flex.base.vo.result.application.DefApplicationResultVO;
import com.luohuo.flex.common.cache.tenant.application.ApplicationCacheKeyBuilder;
import com.luohuo.flex.model.constant.EchoApi;
import com.luohuo.flex.base.manager.application.DefApplicationManager;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 应用管理
 *
 * @author tangyh
 * @version v1.0
 * @date 2021/9/29 1:26 下午
 * @create [2021/9/29 1:26 下午 ] [tangyh] [初始创建]
 */
@RequiredArgsConstructor
@Service(EchoApi.DEF_APPLICATION_SERVICE_IMPL_CLASS)
public class DefApplicationManagerImpl extends SuperCacheManagerImpl<DefApplicationMapper, DefApplication> implements DefApplicationManager {

    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new ApplicationCacheKeyBuilder();
    }


    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return CollHelper.uniqueIndex(find(ids), DefApplication::getId, DefApplication::getName);
    }

    public List<DefApplication> find(Set<Serializable> ids) {
//         强转， 防止数据库隐式转换，  若你的id 是string类型，请勿强转
        return findByIds(ids, null).stream().filter(Objects::nonNull).toList();
    }

    @Override
    public List<DefApplicationResultVO> findMyApplication(String name) {
        return baseMapper.findMyApplication(name);
    }

    @Override
    public List<DefApplicationResultVO> findRecommendApplication(String name) {
        return Collections.emptyList();
    }

    @Override
    public List<DefApplication> findGeneral() {
        return list(Wraps.<DefApplication>lbQ().eq(DefApplication::getIsGeneral, true));
    }

}
