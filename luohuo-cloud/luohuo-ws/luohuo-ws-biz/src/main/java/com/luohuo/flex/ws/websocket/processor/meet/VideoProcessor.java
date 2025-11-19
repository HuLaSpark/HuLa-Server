package com.luohuo.flex.ws.websocket.processor.meet;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.model.ws.WSBaseReq;
import com.luohuo.flex.model.enums.WSReqTypeEnum;
import com.luohuo.flex.ws.service.RoomTimeoutService;
import com.luohuo.flex.ws.service.VideoChatService;
import com.luohuo.flex.ws.vo.HeartbeatReq;
import com.luohuo.flex.ws.websocket.processor.MessageProcessor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import static com.luohuo.flex.model.enums.WSReqTypeEnum.*;

/**
 * 视频信令处理器
 * 功能：
 * 1. 处理用户加入/离开视频房间
 * 2. 转发点对点和群组视频信令
 * 3. 处理视频心跳保活
 *
 * 私聊：包含视频、语音两个功能
 * 群聊：只有视频聊天，可以选择关闭视频画面
 */
@Slf4j
@Order(10)
@Component
@RequiredArgsConstructor
public class VideoProcessor implements MessageProcessor {
    private final VideoChatService videoService;
	private final RoomTimeoutService roomTimeoutService;

    @Override
    public boolean supports(WSBaseReq req) {
		return WEBRTC_SIGNAL.eq(req.getType()) ||
				VIDEO_HEARTBEAT.eq(req.getType());
    }

    @Override
    public void process(WebSocketSession session, Long uid, WSBaseReq baseReq) {

		// 处理不同情况的会话消息
		switch (WSReqTypeEnum.of(baseReq.getType())) {
			// 信令在这里单独处理
			case WEBRTC_SIGNAL -> {
				VideoSignalReq signalReq = JSONUtil.toBean(baseReq.getData(), VideoSignalReq.class);
				videoService.forwardSignal(uid, signalReq.getRoomId(), signalReq.getSignal(), signalReq.getSignalType());
			}
			case VIDEO_HEARTBEAT -> {
				HeartbeatReq heartbeat = JSONUtil.toBean(baseReq.getData(), HeartbeatReq.class);
				roomTimeoutService.refreshRoomActivity(heartbeat.getRoomId());
			}
		}
    }

    @Data
    private static class VideoSignalReq extends WSBaseReq {
		private Long targetUid;
        private Long roomId;
        private String signal; // WebRTC信令内容
		private String signalType; // offer=发起通话 answer=接收通话 candidate=
    }
}