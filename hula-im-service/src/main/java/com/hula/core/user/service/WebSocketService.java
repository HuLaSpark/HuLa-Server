package com.hula.core.user.service;

import com.hula.common.domain.dto.LoginMessageDTO;
import com.hula.common.domain.dto.ScanSuccessMessageDTO;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.req.ws.WSAuthorize;
import io.netty.channel.Channel;

/**
 * @author nyh
 */
public interface WebSocketService {
    /**
     * 处理用户登录请求，需要返回一张带code的二维码
     *
     * @param channel 通道
     */
    void handleLogin(Channel channel);

    /**
     * 处理所有ws连接的事件
     *
     * @param channel 通道
     */
    void connect(Channel channel);

    /**
     * 处理ws断开连接的事件
     *
     * @param channel 通道
     */
    void remove(Channel channel);

    /**
     * 主动认证登录
     *
     * @param channel 通道
     * @param wsAuthorize 认证
     */
    void authorize(Channel channel, WSAuthorize wsAuthorize);

    /**
     * 扫码用户登录成功通知,清除本地Cache中的loginCode和channel的关系
     *
     * @param loginMessageDTO 参数
     */
    void scanLoginSuccess(LoginMessageDTO loginMessageDTO);

    /**
     * 通知用户扫码成功
     *
     * @param scanSuccessMessageDTO 参数
     */
    void scanSuccess(ScanSuccessMessageDTO scanSuccessMessageDTO);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    void sendAll(WsBaseResp<?> wsBaseResp, Long skipUid);


    void sendUser(WsBaseResp<?> wsBaseResp, Long uid);

}
