package com.luohuo.flex.msg.manager.impl;

import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.impl.SuperManagerImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.flex.msg.entity.DefInterfaceProperty;
import com.luohuo.flex.msg.manager.DefInterfacePropertyManager;
import com.luohuo.flex.msg.mapper.DefInterfacePropertyMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通用业务实现类
 * 接口属性
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-04 15:51:37
 * @create [2022-07-04 15:51:37] [zuihou] [代码生成器生成]
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DefInterfacePropertyManagerImpl extends SuperManagerImpl<DefInterfacePropertyMapper, DefInterfaceProperty> implements DefInterfacePropertyManager {
    @Override
    public Map<String, Object> listByInterfaceId(Long id) {
        List<DefInterfaceProperty> propertyList = list(Wraps.<DefInterfaceProperty>lbQ().eq(DefInterfaceProperty::getInterfaceId, id));
        Map<String, Object> param = MapUtil.newHashMap();
        propertyList.forEach(item -> param.put(item.getKey(), item.getValue()));
        return param;
    }
}


