package com.luohuo.flex.base.service.application.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;

import com.luohuo.flex.base.entity.application.DefUserApplication;
import com.luohuo.flex.base.manager.application.DefUserApplicationManager;
import com.luohuo.flex.base.service.application.DefUserApplicationService;

/**
 * <p>
 * 业务实现类
 * 用户的默认应用
 * </p>
 *
 * @author 乾乾
 * @date 2022-03-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class DefUserApplicationServiceImpl extends SuperServiceImpl<DefUserApplicationManager, Long, DefUserApplication> implements DefUserApplicationService {
    @Override
    public Long getMyDefAppByUserId(Long userId) {
        DefUserApplication userApplication = superManager.getOne(Wraps.<DefUserApplication>lbQ().eq(DefUserApplication::getUserId, userId), false);
        return userApplication != null ? userApplication.getApplicationId() : null;
    }
}
