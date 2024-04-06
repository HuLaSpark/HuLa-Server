package com.hula.common.user.dao;

import com.hula.common.user.domain.entity.User;
import com.hula.common.user.mapper.UserMapper;
import com.hula.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author nyh
 * @since 2024-04-06
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

}
