package com.hula.core.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author nyh
 */
public interface WXMsgService {
    /**
     * 用户扫码成功
     */
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage);

    /**
     * 用户授权
     **/
    void authorize(WxOAuth2UserInfo userInfo);
}
