package com.luohuo.basic.tenant.core.db;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.luohuo.basic.context.ContextUtil;

public class DynamicTableHandler implements TableNameHandler {
    @Override
    public String dynamicTableName(String sql, String tableName) {
        Long tenantId = ContextUtil.getRequiredTenantId();
        return tableName + "_" + tenantId;
    }
}
