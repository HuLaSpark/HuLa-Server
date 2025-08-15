package com.luohuo.flex.im.manager.tenant;

import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.flex.im.entity.tenant.DefDatasourceConfig;

/**
 * <p>
 * 通用业务层
 * 数据源
 * </p>
 *
 * @author tangyh
 * @version v1.0
 * @date 2021/9/29 1:26 下午
 * @create [2021/9/29 1:26 下午 ] [tangyh] [初始创建]
 */
public interface DefDatasourceConfigManager extends SuperManager<DefDatasourceConfig> {

    /**
     * 根据名称查询
     *
     * @param dsName
     * @return
     */
    DefDatasourceConfig getByName(String dsName);
}
