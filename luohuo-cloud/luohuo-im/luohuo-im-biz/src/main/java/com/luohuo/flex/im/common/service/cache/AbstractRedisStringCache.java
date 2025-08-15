package com.luohuo.flex.im.common.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import org.springframework.data.util.Pair;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * redis string类型的批量缓存框架
 */
public abstract class AbstractRedisStringCache<IN, OUT> implements BatchCache<IN, OUT> {

    private Class<OUT> outClass;

	@Resource
	private CachePlusOps cachePlusOps;

	protected <T> void refresh(Map<String, T> map) {
		cachePlusOps.mSet(map, -1);
	}

    protected AbstractRedisStringCache() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.outClass = (Class<OUT>) genericSuperclass.getActualTypeArguments()[1];
    }

    protected abstract String getKey(IN req);

    protected abstract Long getExpireSeconds();

    protected abstract Map<IN, OUT> load(List<IN> req);

    @Override
    public OUT get(IN req) {
        return getBatch(Collections.singletonList(req)).get(req);
    }

    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        if (CollectionUtil.isEmpty(req)) {//防御性编程
            return new HashMap<>();
        }
        //去重
        req = req.stream().distinct().collect(Collectors.toList());
        //组装key
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        //批量get
        List<OUT> valueList = cachePlusOps.mGet(keys, outClass);
        //差集计算
        List<IN> loadReqs = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            if (Objects.isNull(valueList.get(i))) {
                loadReqs.add(req.get(i));
            }
        }
        Map<IN, OUT> load = new HashMap<>();
        //不足的重新加载进redis
        if (CollectionUtil.isNotEmpty(loadReqs)) {
            //批量load
            load = load(loadReqs);
            Map<String, OUT> loadMap = load.entrySet().stream()
                    .map(a -> Pair.of(getKey(a.getKey()), a.getValue()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
			cachePlusOps.mSet(loadMap, getExpireSeconds());
        }

        //组装最后的结果
        Map<IN, OUT> resultMap = new HashMap<>();
        for (int i = 0; i < req.size(); i++) {
            IN in = req.get(i);
            OUT out = Optional.ofNullable(valueList.get(i))
                    .orElse(load.get(in));
            resultMap.put(in, out);
        }
        return resultMap;
    }

    @Override
    public void delete(IN req) {
        deleteBatch(Collections.singletonList(req));
    }

    @Override
    public void deleteBatch(List<IN> req) {
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
		List<CacheKey> keyList = new ArrayList<>();
		keys.forEach(item -> {
			CacheKey cacheKey = new CacheKey(item);
			keyList.add(cacheKey);
		});
		cachePlusOps.del(keyList);
    }
}
