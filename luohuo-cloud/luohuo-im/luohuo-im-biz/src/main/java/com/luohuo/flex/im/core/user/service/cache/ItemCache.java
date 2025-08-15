package com.luohuo.flex.im.core.user.service.cache;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.core.user.dao.ItemConfigDao;
import com.luohuo.flex.im.domain.entity.ItemConfig;

import java.util.List;


/**
 * 用户相关缓存
 * @author nyh
 */
@Component
public class ItemCache {
    // todo 多级缓存

    @Resource
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "luohuo:item", key = "'itemsByType:'+#type")
    public List<ItemConfig> getByType(Integer type) {
        return itemConfigDao.getByType(type);
    }

    @Cacheable(cacheNames = "luohuo:item", key = "'item:'+#itemId")
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
