package com.luohuo.flex.base.mapper.system;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.system.DefClient;

/**
 * <p>
 * Mapper 接口
 * 客户端
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-13
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefClientMapper extends SuperMapper<DefClient> {

}
