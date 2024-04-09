package com.hula.common.websocket.domain.service.adapter;

import com.hula.common.user.domain.entity.User;
import com.hula.common.websocket.domain.enums.WSBaseResp;
import com.hula.common.websocket.domain.enums.WSRespTypeEnum;
import com.hula.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.hula.common.websocket.domain.vo.resp.WSLoginUrl;
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
}
