package com.luohuo.flex.generator.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.generator.entity.DefGenTable;

/**
 * <p>
 * Mapper 接口
 * 代码生成
 * </p>
 *
 * @author 乾乾
 * @date 2022-03-01
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefGenTableMapper extends SuperMapper<DefGenTable> {

}
