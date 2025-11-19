package com.luohuo.flex.base.manager.tenant;

import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.basic.interfaces.echo.LoadService;
import com.luohuo.flex.base.entity.tenant.DefTenant;
import com.luohuo.flex.base.vo.result.user.DefTenantResultVO;

import java.util.List;

/**
 * <p>
 * 企业租户
 * </p>
 *
 */
public interface DefTenantManager extends SuperCacheManager<DefTenant>, LoadService {
    /**
     * 查询所有可用的租户
     *
     * @return
     */
    List<DefTenant> listNormal();

    /**
     * 查询用户的可用企业
     *
     * @param userId 用户id
     * @return
     */
    List<DefTenantResultVO> listTenantByUserId(Long userId);
}
