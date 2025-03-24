package com.hula.core.user.service;

import com.hula.core.user.domain.vo.req.user.BindEmailReq;

public interface EmailService {

    // 发送验证码邮件
    Boolean sendVerificationCode(Long uid, BindEmailReq req);
}