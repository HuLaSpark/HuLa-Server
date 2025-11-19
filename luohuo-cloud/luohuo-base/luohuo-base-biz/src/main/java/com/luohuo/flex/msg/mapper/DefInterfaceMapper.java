package com.luohuo.flex.msg.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.msg.entity.DefInterface;

/**
 * <p>
 * Mapper 接口
 * 接口
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-04 16:45:45
 * @create [2022-07-04 16:45:45] [zuihou] [代码生成器生成]
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefInterfaceMapper extends SuperMapper<DefInterface> {

}


