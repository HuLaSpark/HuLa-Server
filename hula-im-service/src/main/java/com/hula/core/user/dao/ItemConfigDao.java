package com.hula.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.mapper.LambdaQueryWrapperX;
import com.hula.core.user.domain.entity.ItemConfig;
import com.hula.core.user.mapper.ItemConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

    public List<ItemConfig> getByType(Integer type) {
        return lambdaQuery()
                .eq(ItemConfig::getType, type)
                .list();
    }

    public ItemConfig getByDesc(String desc) {
        return baseMapper.selectOne(new LambdaQueryWrapperX<ItemConfig>()
                .eq(ItemConfig::getDescribe, desc));
    }
}
