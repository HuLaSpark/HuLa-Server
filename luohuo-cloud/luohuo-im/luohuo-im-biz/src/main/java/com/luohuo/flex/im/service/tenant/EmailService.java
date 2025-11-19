package com.luohuo.flex.im.service.tenant;

import com.luohuo.flex.model.vo.query.BindEmailReq;

public interface EmailService {

    // 发送验证码邮件
    Boolean sendVerificationCode(BindEmailReq req);
}