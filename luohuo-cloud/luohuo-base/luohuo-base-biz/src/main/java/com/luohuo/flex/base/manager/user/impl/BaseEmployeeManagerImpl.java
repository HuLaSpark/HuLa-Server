package com.luohuo.flex.base.manager.user.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.impl.SuperCacheManagerImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.manager.user.BaseEmployeeManager;
import com.luohuo.flex.base.mapper.user.BaseEmployeeMapper;
import com.luohuo.flex.base.vo.query.user.BaseEmployeePageQuery;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import com.luohuo.flex.common.cache.base.user.EmployeeCacheKeyBuilder;

import java.util.List;

/**
 * <p>
 * 通用业务实现类
 * 员工
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 * @create [2021-10-18] [zuihou] [代码生成器生成]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseEmployeeManagerImpl extends SuperCacheManagerImpl<BaseEmployeeMapper, BaseEmployee> implements BaseEmployeeManager {

    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new EmployeeCacheKeyBuilder();
    }

    @Override
    public List<BaseEmployeeResultVO> listEmployeeByUserId(Long userId) {
        return baseMapper.listEmployeeByUserId(userId);
    }

    @Override
    public IPage<BaseEmployeeResultVO> selectPageResultVO(IPage<BaseEmployee> page, Wrapper<BaseEmployee> wrapper, BaseEmployeePageQuery model) {
        return baseMapper.selectPageResultVO(page, wrapper, model);
    }

    @Override
    public BaseEmployee getEmployeeByUser(Long userId) {
        ArgumentAssert.notNull(userId, "用户id为空");
        return baseMapper.selectOne(Wraps.<BaseEmployee>lbQ().eq(BaseEmployee::getUserId, userId));
    }
}
