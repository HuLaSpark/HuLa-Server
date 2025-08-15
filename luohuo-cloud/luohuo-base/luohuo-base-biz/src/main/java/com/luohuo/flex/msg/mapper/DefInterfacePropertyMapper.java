package com.luohuo.flex.msg.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.msg.entity.DefInterfaceProperty;

/**
 * <p>
 * Mapper 接口
 * 接口属性
 * </p>
 *
 * @author zuihou
 * @date 2022-07-04 15:51:37
 * @create [2022-07-04 15:51:37] [zuihou] [代码生成器生成]
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefInterfacePropertyMapper extends SuperMapper<DefInterfaceProperty> {

}


