package com.luohuo.flex.base.mapper.application;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.application.DefUserApplication;

/**
 * <p>
 * Mapper 接口
 * 用户的默认应用
 * </p>
 *
 * @author 乾乾
 * @date 2022-03-06
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefUserApplicationMapper extends SuperMapper<DefUserApplication> {

}
