package com.hula.common.user.service.impl;

import com.hula.common.user.service.LoginService;
import com.hula.common.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author nyh
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean verify(String token) {
        return false;
    }

    @Override
    public void renewalTokenIfNecessary(String token) {

    }

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        return null;
    }

    @Override
    public Long getValidUid(String token) {
        return null;
    }
}
