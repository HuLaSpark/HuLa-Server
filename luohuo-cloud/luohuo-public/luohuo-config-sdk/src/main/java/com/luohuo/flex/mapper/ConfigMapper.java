package com.luohuo.flex.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.entity.Config;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 系统配置
 * </p>
 *
 * @author 乾乾
 * @date 2024-04-18
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ConfigMapper extends SuperMapper<Config> {

}
