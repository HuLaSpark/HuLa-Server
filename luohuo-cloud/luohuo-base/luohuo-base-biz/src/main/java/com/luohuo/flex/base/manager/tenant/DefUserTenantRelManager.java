package com.luohuo.flex.base.manager.tenant;

import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.flex.base.entity.tenant.DefUserTenantRel;
import com.luohuo.flex.base.vo.query.tenant.DefUserTenantRelResultVO;

import java.util.List;

/**
 * <p>
 * 通用业务接口
 * 员工
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-27
 */
public interface DefUserTenantRelManager extends SuperCacheManager<DefUserTenantRel> {
    /**
     * 根据用户id查询员工
     *
     * @param userId 用户id
     * @return
     */
    List<DefUserTenantRelResultVO> listEmployeeByUserId(Long userId);
}
