package com.luohuo.flex.ws.websocket.processor.meet;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.ws.WSBaseReq;
import com.luohuo.flex.ws.service.VideoChatService;
import com.luohuo.flex.ws.vo.MediaControlVO;
import com.luohuo.flex.ws.websocket.processor.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import static com.luohuo.flex.model.enums.WSReqTypeEnum.*;

/**
 * 媒体控制处理器
 *
 * 功能：
 * 1. 处理音频静音控制
 * 2. 处理视频摄像头开关控制
 * 3. 转发媒体控制指令给房间成员
 *
 * 支持的消息类型：
 * - MEDIA_MUTE_AUDIO：静音控制
 * - MEDIA_MUTE_VIDEO：摄像头控制
 */
@Slf4j
@Order(12)
@Component
@RequiredArgsConstructor
public class MediaControlProcessor implements MessageProcessor {
    private final VideoChatService videoService;

    @Override
    public boolean supports(WSBaseReq req) {
        return MEDIA_MUTE_AUDIO.eq(req.getType()) ||
				MEDIA_MUTE_ALL.eq(req.getType()) ||
               	MEDIA_MUTE_VIDEO.eq(req.getType());
    }

    @Override
    public void process(WebSocketSession session, Long uid, WSBaseReq baseReq) {
        MediaControlVO control = JSONUtil.toBean(baseReq.getData(), MediaControlVO.class);
        
        // 转发媒体控制指令给房间内其他成员
        WsBaseResp<MediaControlVO> resp = new WsBaseResp<>();
        resp.setType(WSRespTypeEnum.MediaControl.getType());
        resp.setData(control);
        
        videoService.forwardControlSignal(uid, control.getRoomId(), resp);
    }
}