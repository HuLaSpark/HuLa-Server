package com.luohuo.flex.base.mapper.system;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.system.DefArea;

/**
 * <p>
 * Mapper 接口
 * 地区表
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-13
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefAreaMapper extends SuperMapper<DefArea> {

}
