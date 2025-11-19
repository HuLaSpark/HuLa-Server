package com.luohuo.flex.base.service.tenant;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.luohuo.basic.base.service.SuperCacheService;
import com.luohuo.flex.base.entity.tenant.DefUserTenantRel;
import com.luohuo.flex.base.vo.query.tenant.DefUserTenantRelResultVO;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 员工
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-27
 */
public interface DefUserTenantRelService extends SuperCacheService<Long, DefUserTenantRel> {
    /**
     * 查询数量
     *
     * @param queryWrapper queryWrapper
     * @return long
     * @author tangyh
     * @date 2022/10/28 4:57 PM
     * @create [2022/10/28 4:57 PM ] [tangyh] [初始创建]
     */
    default long count(Wrapper<DefUserTenantRel> queryWrapper) {
        return getSuperManager().count(queryWrapper);
    }

    /**
     * 根据用户id查询员工
     *
     * @param userId 用户id
     * @return
     */
    List<DefUserTenantRelResultVO> listEmployeeByUserId(Long userId);

    /**
     * 查询指定企业指定用户的员工信息
     *
     * @param tenantId 企业id
     * @param userId   用户id
     * @return
     */
    DefUserTenantRel getEmployeeByTenantAndUser(Long tenantId, Long userId);

    /**
     * 设置用户（userId) 的默认企业为 tenantId
     *
     * @param tenantId 企业id
     * @param userId   用户id
     * @return
     */
    boolean updateDefaultTenant(Long tenantId, Long userId);
}
