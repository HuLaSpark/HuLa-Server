package com.hula.core.websocket.domain.service.adapter;

import com.hula.core.user.domain.entity.User;
import com.hula.core.websocket.domain.enums.WSBaseResp;
import com.hula.core.websocket.domain.enums.WSRespTypeEnum;
import com.hula.core.websocket.domain.vo.resp.WSLoginSuccess;
import com.hula.core.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * @author nyh
 */
public class WebSocketAdapter {
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    public static WSBaseResp<?> buildResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess build = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .build();
        resp.setData(build);
        return resp;
    }

    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }
}
