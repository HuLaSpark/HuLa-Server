package com.luohuo.flex.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.flex.oauth.service.ParamService;
import com.luohuo.flex.base.manager.system.DefParameterManager;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 乾乾
 * @date 2021/10/7 13:27
 */
@Service
@RequiredArgsConstructor
public class ParamServiceImpl implements ParamService {
    private final DefParameterManager defParameterManager;

    /**
     * 先从base库查， 若base库没有，在去def库查。
     * 若2个库都有，采用base库的数据
     *
     * @param paramsKeys 字典key
     * @return
     */
    @Override
    public Map<String, String> findParamMapByKey(List<String> paramsKeys) {
        if (CollUtil.isEmpty(paramsKeys)) {
            return Collections.emptyMap();
        }
        // 查询不在base的参数
        Map<String, String> defMap = defParameterManager.findParamMapByKey(paramsKeys);

        Map<String, String> map = MapUtil.newHashMap();
        map.putAll(defMap);
        return map;
    }


    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> paramKeys) {
        if (CollUtil.isEmpty(paramKeys)) {
            return Collections.emptyMap();
        }

        // 查询不在base的参数
        Map<Serializable, Object> defMap = defParameterManager.findByIds(paramKeys);

        HashMap<Serializable, Object> map = MapUtil.newHashMap();
        map.putAll(defMap);

        return map;
    }
}
