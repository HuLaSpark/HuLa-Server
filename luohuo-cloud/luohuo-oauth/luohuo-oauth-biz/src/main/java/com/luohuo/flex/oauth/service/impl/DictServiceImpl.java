package com.luohuo.flex.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.echo.properties.EchoProperties;
import com.luohuo.basic.interfaces.BaseEnum;
import com.luohuo.basic.utils.ClassUtils;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.basic.utils.StrPool;
import com.luohuo.flex.common.properties.SystemProperties;
import com.luohuo.flex.model.vo.result.Option;
import com.luohuo.flex.oauth.service.DictService;
import com.luohuo.flex.oauth.vo.param.CodeQueryVO;
import com.luohuo.flex.base.manager.system.DefDictManager;
import com.luohuo.flex.base.vo.result.system.DefDictItemResultVO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * @author 乾乾
 * @date 2021/10/7 13:27
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DictServiceImpl implements DictService {
    private final DefDictManager defDictManager;
    private final EchoProperties echoProperties;
    private final SystemProperties systemProperties;


    private static final Map<String, Map<String, String>> ENUM_MAP = new HashMap<>();
    private static final Map<String, List<Option>> ENUM_LIST_MAP = new HashMap<>();
    /**
     * 过滤那些枚举
     */
    private static final Predicate<Class<?>> CLASS_FILTER = item -> item != null && item.isEnum() && item.isEnum() && MybatisEnumTypeHandler.isMpEnums(item);

    @PostConstruct
    public void init() {
        String enumPackage = systemProperties.getEnumPackage();
        if (StrUtil.isEmpty(enumPackage)) {
            log.warn("请在配置文件中配置{}.enumPackage", SystemProperties.PREFIX);
            return;
        }
        Set<Class<?>> enumClass = ClassUtils.scanPackage(enumPackage, CLASS_FILTER);

        StringJoiner enumSb = new StringJoiner(StrPool.COMMA);
        enumClass.forEach(item -> {
            Object[] enumConstants = item.getEnumConstants();
            BaseEnum[] baseEnums = Arrays.stream(enumConstants).map(i -> (BaseEnum) i).toArray(BaseEnum[]::new);

            ENUM_LIST_MAP.put(item.getSimpleName(), Option.mapOptions(baseEnums));
            ENUM_MAP.put(item.getSimpleName(), CollHelper.getMap(baseEnums));
            enumSb.add(item.getSimpleName());
        });

        log.info("扫描: {} ,共加载了{}个枚举类, 分别为: {}", enumPackage, ENUM_MAP.size(), enumSb);
    }

    /**
     * 先从base库查， 若base库没有，在去def库查。
     * 若2个库都有，采用base库的数据
     *
     * @param dictKeys 字典key
     * @return
     */
    @Override
    public Map<String, List<DefDictItemResultVO>> findDictMapByType(List<String> dictKeys) {
        if (CollUtil.isEmpty(dictKeys)) {
            return Collections.emptyMap();
        }

        // 查询不在base的字典
        Map<String, List<DefDictItemResultVO>> defMap = defDictManager.findDictMapItemListByKey(dictKeys);

        Map<String, List<DefDictItemResultVO>> map = MapUtil.newHashMap();
        map.putAll(defMap);
        return map;
    }


    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> dictKeys) {
        if (CollUtil.isEmpty(dictKeys)) {
            return Collections.emptyMap();
        }

        Map<Serializable, Object> defMap = defDictManager.findByIds(dictKeys);
        HashMap<Serializable, Object> map = MapUtil.newHashMap();
        map.putAll(defMap);

        return map;
    }


    @Override
    public List<Option> findEnumByType(CodeQueryVO type) {
        Map<String, List<Option>> result = findEnumMapByType(Collections.singletonList(type));
        return result.getOrDefault(type.getType(), Collections.emptyList());
    }

    @Override
    public Map<String, List<Option>> findEnumMapByType(List<CodeQueryVO> types) {
        if (CollUtil.isEmpty(types)) {
            return ENUM_LIST_MAP;
        }
        Map<String, CodeQueryVO> codeMap = MapUtil.newHashMap();
        if (CollUtil.isNotEmpty(types)) {
            types.forEach(item -> codeMap.put(item.getType(), item));
        }

        Map<String, List<Option>> map = new HashMap<>(CollHelper.initialCapacity(types.size()));
        for (CodeQueryVO type : types) {
            if (!ENUM_LIST_MAP.containsKey(type.getType())) {
                continue;
            }
            List<Option> cacheOptions = ENUM_LIST_MAP.get(type.getType());

            CodeQueryVO codeQuery = codeMap.get(type.getType());
            boolean extendFirst = codeQuery == null || codeQuery.getExtendFirst() == null || codeQuery.getExtendFirst();
            List<Option> options = new ArrayList<>();
            if (codeQuery != null && extendFirst && codeQuery.getExtend() != null) {
                options.add(codeQuery.getExtend());
            }

            List<Option> optionList = cacheOptions.stream().filter(item -> {
                if (codeQuery != null) {
                    List<String> excludes = codeQuery.getExcludes() == null ? Collections.emptyList() : codeQuery.getExcludes();
                    return !excludes.contains(item.getValue());
                }
                return false;
            }).toList();
            options.addAll(optionList);

            if (codeQuery != null && !extendFirst && codeQuery.getExtend() != null) {
                options.add(codeQuery.getExtend());
            }
            map.put(type.getType(), options);
        }
        return map;
    }

    @Override
    public Map<String, List<Option>> mapOptionByDict(Map<String, List<DefDictItemResultVO>> map, List<CodeQueryVO> codeQueryVO) {
        if (MapUtil.isEmpty(map)) {
            return Collections.emptyMap();
        }
        Map<String, CodeQueryVO> codeMap = MapUtil.newHashMap();
        if (CollUtil.isNotEmpty(codeQueryVO)) {
            codeQueryVO.forEach(item -> codeMap.put(item.getType(), item));
        }

        Map<String, List<Option>> newMap = MapUtil.newHashMap();
        map.forEach((type, values) -> {
            CodeQueryVO codeQuery = codeMap.get(type);
            boolean extendFirst = codeQuery == null || codeQuery.getExtendFirst() == null || codeQuery.getExtendFirst();

            List<Option> options = new ArrayList<>();
            if (codeQuery != null && extendFirst && codeQuery.getExtend() != null) {
                options.add(codeQuery.getExtend());
            }
            List<Option> optionList = values.stream().filter(item -> {
                        if (codeQuery != null) {
                            List<String> excludes = codeQuery.getExcludes() == null ? Collections.emptyList() : codeQuery.getExcludes();
                            return !excludes.contains(item.getKey());
                        }
                        return false;
                    })
                    .map(item -> Option.builder().label(item.getName())
                            .text(item.getName()).value(item.getKey()).color(item.getCssClass()).build()).toList();
            options.addAll(optionList);
            if (codeQuery != null && !extendFirst && codeQuery.getExtend() != null) {
                options.add(codeQuery.getExtend());
            }

            newMap.put(type, options);
        });
        return newMap;
    }

    @Override
    public List<Option> mapOptionByDict(Map<String, List<DefDictItemResultVO>> map, CodeQueryVO codeQueryVO) {
        Map<String, List<Option>> result = mapOptionByDict(map, Collections.singletonList(codeQueryVO));
        return result.get(codeQueryVO.getType());
    }
}
