package com.luohuo.flex.base.mapper.tenant;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.tenant.DefTenant;
import com.luohuo.flex.base.vo.result.user.DefTenantResultVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    /**
     * 查询用户的可用企业
     *
     * @param userId 用户id
     * @return
     */
    List<DefTenantResultVO> listTenantByUserId(@Param("userId") Long userId);
}
