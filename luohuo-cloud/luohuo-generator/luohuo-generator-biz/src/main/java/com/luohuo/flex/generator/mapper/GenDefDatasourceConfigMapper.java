package com.luohuo.flex.generator.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.im.entity.tenant.DefDatasourceConfig;

/**
 * <p>
 * Mapper 接口
 * 数据源
 * </p>
 *
 * @author 乾乾
 * @date 2021-09-13
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface GenDefDatasourceConfigMapper extends SuperMapper<DefDatasourceConfig> {

}
