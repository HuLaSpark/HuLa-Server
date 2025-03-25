package com.hula.core.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.hula.core.user.domain.dto.captcha.Captcha;
import com.hula.core.user.domain.dto.captcha.SpecCaptcha;
import com.hula.core.user.service.CaptchaService;
import com.hula.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;

/**
 * 验证码服务
 *
 * @author qianqian
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    /**
	 * 发送验证码：
     * 注意验证码生成需要服务器支持，若您的验证码接口显示不出来，
     */
    public HashMap<String, Object> createImg() {
		String uuid = IdUtil.fastUUID();

		SpecCaptcha specCaptcha = new SpecCaptcha(120, 45, 4);
		// 设置类型，纯数字、纯字母、字母数字混合
		specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
		RedisUtils.hset("numberCode", uuid, specCaptcha.text().toLowerCase(), 300);

		HashMap<String, Object> map = new HashMap<>();
		map.put("uuid", uuid);
		map.put("img", specCaptcha.toBase64());
		return map;
    }
}
