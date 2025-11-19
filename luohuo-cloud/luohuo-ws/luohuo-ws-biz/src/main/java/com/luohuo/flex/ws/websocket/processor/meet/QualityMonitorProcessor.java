package com.luohuo.flex.ws.websocket.processor.meet;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.ws.WSBaseReq;
import com.luohuo.flex.model.enums.WSReqTypeEnum;
import com.luohuo.flex.ws.service.PushService;
import com.luohuo.flex.ws.service.VideoChatService;
import com.luohuo.flex.ws.vo.NetworkQualityVO;
import com.luohuo.flex.ws.vo.ScreenSharingVO;
import com.luohuo.flex.ws.websocket.processor.MessageProcessor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.List;
import static com.luohuo.flex.model.enums.WSReqTypeEnum.NETWORK_REPORT;
import static com.luohuo.flex.model.enums.WSReqTypeEnum.SCREEN_SHARING;

/**
 * 通话质量监控处理器
 *
 * 功能：
 * 1. 处理网络质量报告
 * 2. 管理屏幕共享状态
 *
 * 支持的消息类型：
 * - NETWORK_REPORT：网络质量报告
 * - SCREEN_SHARING：屏幕共享状态变更
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QualityMonitorProcessor implements MessageProcessor {
	private final VideoChatService videoService;
	private final PushService pushService;

    public boolean supports(WSBaseReq req) {
		return NETWORK_REPORT.eq(req.getType()) ||
				SCREEN_SHARING.eq(req.getType());
    }

	@Override
	public void process(WebSocketSession session, Long uid, WSBaseReq baseReq) {
		switch (WSReqTypeEnum.of(baseReq.getType())) {
			case NETWORK_REPORT -> handleNetworkReport(uid, baseReq);
			case SCREEN_SHARING -> handleScreenSharing(uid, baseReq);
		}
	}

	private void handleNetworkReport(Long uid, WSBaseReq baseReq) {
		NetworkReportReq req = JSONUtil.toBean(baseReq.getData(), NetworkReportReq.class);

		// 1. 存储网络质量数据
		videoService.saveNetworkQuality(uid, req.getRoomId(), req.getQuality());

		// 2. 如果质量差，通知房间管理员
		if (req.getQuality() < 0.3) {
			notifyPoorQuality(uid, req.getRoomId(), req.getQuality());
		}
	}

	private void handleScreenSharing(Long uid, WSBaseReq baseReq) {
		ScreenSharingReq req = JSONUtil.toBean(baseReq.getData(), ScreenSharingReq.class);

		// 1. 验证用户是否在房间中
		if (!videoService.isUserInRoom(uid, req.getRoomId())) {
			log.warn("用户不在房间中，无法共享屏幕: uid={}, room={}", uid, req.getRoomId());
			return;
		}

		// 2. 更新屏幕共享状态
		videoService.setScreenSharing(req.getRoomId(), uid, req.isSharing());

		// 3. 通知房间成员
		notifyScreenSharing(req.getRoomId(), uid, req.isSharing());
	}

	private void notifyPoorQuality(Long uid, Long roomId, double quality) {
		// 获取房间管理员
		List<Long> admins = videoService.getRoomAdmins(roomId);

		if (admins.isEmpty()) return;

		WsBaseResp<NetworkQualityVO> resp = new WsBaseResp<>();
		resp.setType(WSRespTypeEnum.NetworkPoor.getType());
		resp.setData(new NetworkQualityVO(roomId, uid, quality));

		// 发送给管理员
		pushService.sendPushMsg(resp, admins, uid);
	}

	private void notifyScreenSharing(Long roomId, Long uid, boolean isSharing) {
		WsBaseResp<ScreenSharingVO> resp = new WsBaseResp<>();
		resp.setType(isSharing ?
				WSRespTypeEnum.ScreenSharingStarted.getType() :
				WSRespTypeEnum.ScreenSharingStopped.getType());
		resp.setData(new ScreenSharingVO(roomId, uid, isSharing));

		List<Long> members = videoService.getRoomMembers(roomId);
		members.remove(uid);

		if (!members.isEmpty()) {
			pushService.sendPushMsg(resp, members, uid);
		}
	}

	@Data
	private static class NetworkReportReq {
		private Long roomId;
		private double quality; // 0.0 ~ 1.0
	}

	@Data
	private static class ScreenSharingReq {
		private Long roomId;
		private boolean sharing;
	}
}