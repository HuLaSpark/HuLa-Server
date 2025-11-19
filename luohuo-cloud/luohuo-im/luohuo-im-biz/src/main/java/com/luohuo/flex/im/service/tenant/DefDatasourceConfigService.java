package com.luohuo.flex.im.service.tenant;

import com.luohuo.basic.base.service.SuperService;
import com.luohuo.flex.im.entity.tenant.DefDatasourceConfig;

/**
 * <p>
 * 业务接口
 * 数据源
 * </p>
 *
 * @author 乾乾
 * @date 2021-09-13
 */
public interface DefDatasourceConfigService extends SuperService<Long, DefDatasourceConfig> {
    /**
     * 测试数据源链接
     *
     * @param id 数据源信息
     * @return
     */
    Boolean testConnection(Long id);
}
