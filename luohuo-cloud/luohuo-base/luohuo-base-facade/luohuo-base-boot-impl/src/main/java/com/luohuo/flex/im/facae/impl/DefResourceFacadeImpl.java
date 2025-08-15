package com.luohuo.flex.im.facae.impl;

import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.im.facade.DefResourceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.base.service.application.DefResourceService;
import com.luohuo.flex.common.cache.tenant.application.AllResourceApiCacheKeyBuilder;
import com.luohuo.flex.model.vo.result.ResourceApiVO;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author tangyh
 * @since 2024/9/21 22:21
 */
@Service
@RequiredArgsConstructor
public class DefResourceFacadeImpl implements DefResourceFacade {
    private final DefResourceService defResourceService;
    private final CacheOps cacheOps;

    @Override
    public Map<String, Set<String>> listAllApi() {
        CacheKey cacheKey = AllResourceApiCacheKeyBuilder.builder();
        CacheResult<Map<String, Set<String>>> result = cacheOps.get(cacheKey, (k) -> findAllApi());
        return result.getValue();
    }

    private Map<String, Set<String>> findAllApi() {
        // 查询系统中配置的URI和权限关系
        List<ResourceApiVO> list = defResourceService.findAllApi();
        return list.stream()
                // 这里单体版和微服务版不同
                .peek(item -> {
                    String uri = item.getUri();
                    if (!StrUtil.startWithAny(uri, "/gateway")) {
                        uri = StrUtil.subSuf(uri, StrUtil.indexOf(uri, '/', 1));
                    }
                    item.setUri(uri);
                })
                .collect(Collectors.toMap(
                        item -> item.getUri() + "###" + item.getRequestMethod(),
                        resourceApiVO -> {
                            Set<String> codes = new HashSet<>();
                            codes.add(resourceApiVO.getCode());
                            return codes;
                        },
                        (existingCodes, newCodes) -> {
                            existingCodes.addAll(newCodes);
                            return existingCodes;
                        },
                        LinkedHashMap::new
                ));
    }
}
