package com.luohuo.flex.file.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.file.entity.Appendix;

/**
 * <p>
 * Mapper 接口
 * 业务附件
 * </p>
 *
 * @author tangyh
 * @date 2021-06-30
 * @create [2021-06-30] [tangyh] [初始创建]
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface AppendixMapper extends SuperMapper<Appendix> {

}
