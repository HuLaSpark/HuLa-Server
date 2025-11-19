package com.luohuo.flex.oauth.controller;

import com.luohuo.basic.annotation.response.IgnoreResponseBodyAdvice;
import com.luohuo.basic.base.R;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.model.vo.query.BindEmailReq;
import com.luohuo.flex.oauth.granter.CaptchaTokenGranter;
import com.luohuo.flex.oauth.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

import static com.luohuo.flex.common.constant.SwaggerConstants.DATA_TYPE_STRING;

/**
 * 验证码服务
 *
 * @author tangyh
 * @version v1.0
 * @date 2022/9/29 5:37 PM
 * @create [2022/9/29 5:37 PM ] [tangyh] [初始创建]
 */
@Slf4j
@RestController
@RequestMapping("/anyTenant")
@AllArgsConstructor
@Tag(name = "验证码")
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 验证验证码
     *
     * @param key  验证码唯一uuid key
     * @param code 验证码
     */
    @Operation(summary = "验证验证码是否正确", description = "验证验证码")
    @GetMapping(value = "/checkCaptcha")
    public R<Boolean> checkCaptcha(@RequestParam(value = "key") String key, @RequestParam(value = "code") String code,
                                   @RequestParam(value = "templateCode", required = false, defaultValue = CaptchaTokenGranter.GRANT_TYPE)
                                   String templateCode) throws BizException {
        return this.captchaService.checkCaptcha(key, templateCode, code);
    }

    @Operation(summary = "获取图片验证码", description = "获取图片验证码")
    @Parameters({@Parameter(name = "key", description = "唯一字符串: 前端随机生成一个唯一字符串用于生成验证码，并将key传给后台用于验证", schema = @Schema(type = DATA_TYPE_STRING), in = ParameterIn.QUERY)})
    @GetMapping(value = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    @IgnoreResponseBodyAdvice
    public R<HashMap<String, Object>> captcha(@RequestParam(value = "key", required = false) String key) throws IOException {
        return R.success(this.captchaService.createImg(key));
    }

    @Operation(summary = "发送短信验证码", description = "发送短信验证码")
    @Parameters({
            @Parameter(name = "mobile", description = "手机号", schema = @Schema(type = DATA_TYPE_STRING), in = ParameterIn.QUERY),
            @Parameter(name = "templateCode", description = "模板编码: 在「运营平台」-「消息模板」-「模板标识」配置一个短信模板", schema = @Schema(type = DATA_TYPE_STRING), in = ParameterIn.QUERY),
    })
    @GetMapping(value = "/sendSmsCode")
    public R<Boolean> sendSmsCode(@RequestParam(value = "mobile") String mobile,
                                  @RequestParam(value = "templateCode") String templateCode) {
        return captchaService.sendSmsCode(mobile, templateCode);
    }
    @Parameters({
            @Parameter(name = "email", description = "邮箱", schema = @Schema(type = DATA_TYPE_STRING), in = ParameterIn.QUERY),
            @Parameter(name = "templateCode", description = "模板编码: 在「运营平台」-「消息模板」-「模板标识」配置一个邮件模板", schema = @Schema(type = DATA_TYPE_STRING), in = ParameterIn.QUERY),
    })
    @Operation(summary = "发送邮箱验证码", description = "发送邮箱验证码")
    @PostMapping(value = "/sendEmailCode")
    public R<Long> sendEmailCode(@RequestBody BindEmailReq bindEmailReq) {
        return captchaService.sendEmailCode(bindEmailReq);
    }

}
