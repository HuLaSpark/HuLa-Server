package com.luohuo.flex.im.core.user.service.cache;

import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.common.service.cache.AbstractRedisStringCache;
import com.luohuo.flex.im.core.user.dao.ItemConfigDao;
import com.luohuo.flex.im.domain.entity.ItemConfig;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户徽章基本信息的缓存
 * @author nyh
 */
@Component
public class ItemCache extends AbstractRedisStringCache<Long, ItemConfig> {
	@Resource
	private ItemConfigDao itemConfigDao;

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_ITEM, uid);
    }

	@Override
    protected Long getExpireSeconds() {
        return -1L;
    }

	@Override
    protected Map<Long, ItemConfig> load(List<Long> uidList) {
        List<ItemConfig> needLoadUserList = itemConfigDao.listByIds(uidList);
        return needLoadUserList.stream().collect(Collectors.toMap(ItemConfig::getId, Function.identity()));
    }

	@Cacheable(cacheNames = "luohuo:userItem", key = "'itemsByType:'+#type")
	public List<ItemConfig> getByType(Integer type) {
		return itemConfigDao.getByType(type);
	}

	/**
	 * 获取所有徽章列表
	 */
	@Cacheable(cacheNames = "luohuo:userItem", key = "'allItems'")
	public List<ItemConfig> getAllItems() {
		return itemConfigDao.list();
	}

	/**
	 * 当有徽章数据变更时，清除所有徽章的缓存，确保下次获取时是最新数据
	 * 可以使用@CacheEvict注解
	 */
	@CacheEvict(cacheNames = "luohuo:userItem", key = "'all_items'")
	public void evictAllItemsCache() {
	}
}
