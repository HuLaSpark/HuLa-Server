package com.hula.core.user.service.cache;

import com.hula.core.user.dao.ItemConfigDao;
import com.hula.core.user.domain.entity.ItemConfig;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 用户相关缓存
 * @author nyh
 */
@Component
public class ItemCache {
    //todo 多级缓存

    @Resource
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item", key = "'itemsByType:'+#type")
    public List<ItemConfig> getByType(Integer type) {
        return itemConfigDao.getByType(type);
    }

    @Cacheable(cacheNames = "item", key = "'item:'+#itemId")
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
