package com.luohuo.flex.oauth.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.model.vo.query.BindEmailReq;
import com.luohuo.flex.service.SysConfigService;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.ChineseGifCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.R;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.common.cache.common.CaptchaCacheKeyBuilder;
import com.luohuo.flex.model.enumeration.base.MsgTemplateCodeEnum;
import com.luohuo.flex.msg.vo.update.ExtendMsgSendVO;
import com.luohuo.flex.oauth.granter.CaptchaTokenGranter;
import com.luohuo.flex.oauth.properties.CaptchaProperties;
import com.luohuo.flex.oauth.service.CaptchaService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import static com.luohuo.basic.exception.code.ResponseEnum.CAPTCHA_ERROR;

import com.luohuo.flex.msg.facade.MsgFacade;
/**
 * 验证码服务
 *
 * @author zuihou
 */
@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaServiceImpl implements CaptchaService {

	private final SysConfigService configService;
    private final CacheOps cacheOps;
    private final CaptchaProperties captchaProperties;
    private final MsgFacade msgFacade;
    private final DefUserService defUserService;

    /**
     * 注意验证码生成需要服务器支持，若您的验证码接口显示不出来，
     * 需要在 接口 所在的服务器安装字体库！ 若是docker部署，需要在docker镜像内部安装。
     * <p>
     * centos执行（其他服务器自行百度）：
     * yum install fontconfig
     * fc-cache --force
     *
     * @param key      验证码 uuid
     * @throws IOException
     */
    @Override
    public HashMap<String, Object> createImg(String key) throws IOException {
//		String uuid = IdUtil.fastUUID();
//		long expireTime = 300;
//
//		com.luohuo.flex.system.core.user.domain.dto.captcha.SpecCaptcha specCaptcha = new com.luohuo.flex.system.core.user.domain.dto.captcha.SpecCaptcha(120, 45, 4);
//		// 设置类型，纯数字、纯字母、字母数字混合
//		specCaptcha.setCharType(com.luohuo.flex.system.core.user.domain.dto.captcha.Captcha.TYPE_ONLY_NUMBER);
//		cachePlusOps.hSet(CaptchaCacheKeyBuilder.hashBuild("numberCode", uuid, expireTime), specCaptcha.text().toLowerCase());
//
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("uuid", uuid);
//		map.put("img", specCaptcha.toBase64());
//		return map;

        if (StrUtil.isBlank(key)) {
			key = IdUtil.fastUUID();
        }
        Captcha captcha = createCaptcha();

        CacheKey cacheKey = CaptchaCacheKeyBuilder.build(key, CaptchaTokenGranter.GRANT_TYPE);
        cacheOps.set(cacheKey, captcha.text().toLowerCase());

		HashMap<String, Object> map = new HashMap<>();
		map.put("uuid", key);
		map.put("img", captcha.toBase64());
		return map;
    }


    @Override
    public R<Boolean> sendSmsCode(String mobile, String templateCode) {
        if (MsgTemplateCodeEnum.REGISTER_SMS.eq(templateCode)) {
            // 查user表判断重复
            boolean flag = defUserService.checkMobile(mobile, null);
            ArgumentAssert.isFalse(flag, "该手机号已经被注册");
        } else if (MsgTemplateCodeEnum.MOBILE_LOGIN.eq(templateCode)) {
            //查user表判断是否存在
            boolean flag = defUserService.checkMobile(mobile, null);
            ArgumentAssert.isTrue(flag, "该手机号尚未注册，请先注册后在登陆。");
        } else if (MsgTemplateCodeEnum.MOBILE_EDIT.eq(templateCode)) {
            //查user表判断是否存在
            boolean flag = defUserService.checkMobile(mobile, null);
            ArgumentAssert.isFalse(flag, "该手机号已经被他人使用");
        }

        String code = RandomUtil.randomNumbers(4);
        CacheKey cacheKey = CaptchaCacheKeyBuilder.build(mobile, templateCode);
        // cacheKey.setExpire(Duration.ofMinutes(15));  // 可以修改有效期
        cacheOps.set(cacheKey, code);

        log.info("短信验证码 cacheKey={}, code={}", cacheKey, code);

        // 在「运营平台」-「消息模板」配置一个「模板标识」为 templateCode， 且「模板内容」中需要有 code 占位符
        // 也可以考虑给模板增加一个过期时间等参数
        ExtendMsgSendVO msgSendVO = ExtendMsgSendVO.builder().code(templateCode).build();
        msgSendVO.addParam("code", code);
        msgSendVO.addRecipient(mobile);
        return R.success(msgFacade.sendByTemplate(msgSendVO));
    }

    @Override
    public R<Boolean> sendEmailCode(BindEmailReq bindEmailReq) {
        if (MsgTemplateCodeEnum.REGISTER_EMAIL.eq(bindEmailReq.getTemplateCode())) {
            // 查user表判断重复
            boolean flag = defUserService.checkEmail(bindEmailReq.getEmail(), null);
            ArgumentAssert.isFalse(flag, "该邮箱已经被注册");
        } else if (MsgTemplateCodeEnum.EMAIL_EDIT.eq(bindEmailReq.getTemplateCode())) {
            //查user表判断是否存在
            boolean flag = defUserService.checkEmail(bindEmailReq.getEmail(), null);
            ArgumentAssert.isFalse(flag, "该邮箱已经被他人使用");
        }

        String code = RandomUtil.randomString(6);
        CacheKey cacheKey = CaptchaCacheKeyBuilder.build(bindEmailReq.getEmail(), bindEmailReq.getTemplateCode());
        cacheOps.set(cacheKey, code);

        log.info("邮件验证码 cacheKey={}, code={}", cacheKey, code);

        // 在「运营平台」-「消息模板」配置一个「模板标识」为 templateCode， 且「模板内容」中需要有 code 占位符
		int expireTime = 1800;
        ExtendMsgSendVO msgSendVO = ExtendMsgSendVO.builder().code(bindEmailReq.getTemplateCode()).build();
        msgSendVO.addParam("emailCode", code);
		msgSendVO.addParam("systemName", configService.get("systemName"));
		msgSendVO.addParam("expireMinutes", String.valueOf(expireTime / 60));
		msgSendVO.addParam("adminEmail", configService.get("masterEmail"));
		msgSendVO.addParam("currentTime", TimeUtils.nowToStr());

        msgSendVO.addRecipient(bindEmailReq.getEmail());
        return R.success(msgFacade.sendByTemplate(msgSendVO));
    }

    @Override
    public R<Boolean> checkCaptcha(String key, String templateCode, String value) {
        if (StrUtil.isBlank(value)) {
            return R.fail(CAPTCHA_ERROR.build("请输入验证码"));
        }
        CacheKey cacheKey = CaptchaCacheKeyBuilder.build(key, templateCode);
        CacheResult<String> code = cacheOps.get(cacheKey);
        if (StrUtil.isEmpty(code.getValue())) {
            return R.fail(CAPTCHA_ERROR.build("验证码已过期"));
        }
        if (!StrUtil.equalsIgnoreCase(value, code.getValue())) {
            return R.fail(CAPTCHA_ERROR.build("验证码不正确"));
        }
        cacheOps.del(cacheKey);
        return R.success(true);
    }


    private Captcha createCaptcha() {

        CaptchaProperties.CaptchaType type = captchaProperties.getType();
        Captcha captcha = switch (type) {
            case GIF ->
                    new GifCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), captchaProperties.getLen());
            case SPEC ->
                    new SpecCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), captchaProperties.getLen());
            case CHINESE ->
                    new ChineseCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), captchaProperties.getLen());
            case CHINESE_GIF ->
                    new ChineseGifCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), captchaProperties.getLen());
            default ->
                    new ArithmeticCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), captchaProperties.getLen());
        };
        captcha.setCharType(captchaProperties.getCharType());

        return captcha;
    }

    private void setHeader(HttpServletResponse response) {
        response.setContentType(captchaProperties.getType().getContentType());
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }
}
