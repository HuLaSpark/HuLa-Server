package com.luohuo.flex.base.service.user.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;
import com.luohuo.flex.base.entity.user.BaseOrgRoleRel;
import com.luohuo.flex.base.manager.user.BaseOrgRoleRelManager;
import com.luohuo.flex.base.service.user.BaseOrgRoleRelService;


/**
 * <p>
 * 业务实现类
 * 组织的角色
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class BaseOrgRoleRelServiceImpl extends SuperServiceImpl<BaseOrgRoleRelManager, Long, BaseOrgRoleRel> implements BaseOrgRoleRelService {

}
