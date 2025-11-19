package com.luohuo.flex.base.mapper.tenant;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.tenant.DefTenant;
import org.springframework.stereotype.Repository;


/**
 * <p>
 * Mapper 接口
 * 企业租户
 * </p>
 *
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefTenantMapper extends SuperMapper<DefTenant> {
}
