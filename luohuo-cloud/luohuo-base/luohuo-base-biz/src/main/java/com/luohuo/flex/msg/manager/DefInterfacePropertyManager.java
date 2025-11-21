package com.luohuo.flex.msg.manager;

import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.flex.msg.entity.DefInterfaceProperty;

import java.util.Map;

/**
 * <p>
 * 通用业务接口
 * 接口属性
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-04 15:51:37
 * @create [2022-07-04 15:51:37] [zuihou] [代码生成器生成]
 */
public interface DefInterfacePropertyManager extends SuperManager<DefInterfaceProperty> {
    /**
     * 根据接口ID查询接口属性参数
     *
     * @param id
     * @return
     */
    Map<String, Object> listByInterfaceId(Long id);
}


