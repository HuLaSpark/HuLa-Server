package com.hula.core.user.service.impl;

import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nyh
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    @Transactional
    public Long register(User insert) {
        userDao.save(insert);
        // TODO 用户注册事件 (nyh -> 2024-04-10 00:31:06)
        return insert.getId();
    }
}
