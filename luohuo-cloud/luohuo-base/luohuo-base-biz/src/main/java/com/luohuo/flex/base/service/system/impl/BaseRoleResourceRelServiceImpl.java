package com.luohuo.flex.base.service.system.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;
import com.luohuo.flex.base.entity.system.BaseRoleResourceRel;
import com.luohuo.flex.base.manager.system.BaseRoleResourceRelManager;
import com.luohuo.flex.base.service.system.BaseRoleResourceRelService;


/**
 * <p>
 * 业务实现类
 * 角色的资源
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class BaseRoleResourceRelServiceImpl extends SuperServiceImpl<BaseRoleResourceRelManager, Long, BaseRoleResourceRel>
        implements BaseRoleResourceRelService {
}
