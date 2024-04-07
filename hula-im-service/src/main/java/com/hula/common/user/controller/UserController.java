package com.hula.common.user.controller;


import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author nyh
 * @since 2024-04-06
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private WxMpService wxMpService;

    @GetMapping("test")
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }
}

