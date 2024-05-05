package com.hula.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.user.domain.entity.Role;
import com.hula.core.user.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author nyh
 * @since 2024-05-05
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
