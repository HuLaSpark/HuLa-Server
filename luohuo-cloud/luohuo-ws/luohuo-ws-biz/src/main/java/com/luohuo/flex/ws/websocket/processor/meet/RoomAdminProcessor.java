package com.luohuo.flex.ws.websocket.processor.meet;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.enums.CallStatusEnum;
import com.luohuo.flex.model.ws.WSBaseReq;
import com.luohuo.flex.model.enums.WSReqTypeEnum;
import com.luohuo.flex.ws.service.PushService;
import com.luohuo.flex.ws.service.RoomTimeoutService;
import com.luohuo.flex.ws.service.VideoChatService;
import com.luohuo.flex.ws.vo.AllMutedVO;
import com.luohuo.flex.ws.vo.UserKickedVO;
import com.luohuo.flex.ws.websocket.processor.MessageProcessor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;

import static com.luohuo.flex.model.enums.WSReqTypeEnum.*;

/**
 * 管理员控制
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoomAdminProcessor implements MessageProcessor {
	private final VideoChatService videoService;
	private final PushService pushService;
	private final RoomTimeoutService roomTimeoutService;

    public boolean supports(WSBaseReq req) {
		return CLOSE_ROOM.eq(req.getType()) ||
				KICK_USER.eq(req.getType()) ||
				MEDIA_MUTE_ALL.eq(req.getType());
    }

	@Override
	public void process(WebSocketSession session, Long uid, WSBaseReq baseReq) {
		switch (WSReqTypeEnum.of(baseReq.getType())) {
			case CLOSE_ROOM -> handleCloseRoom(uid, baseReq);
			case KICK_USER -> handleKickUser(uid, baseReq);
			case MEDIA_MUTE_ALL -> handleMuteAll(uid, baseReq);
		}
	}


	private void handleCloseRoom(Long operatorId, WSBaseReq baseReq) {
		CloseRoomReq req = JSONUtil.toBean(baseReq.getData(), CloseRoomReq.class);

		// 1. 验证操作者权限（需是房间创建者或管理员）
		if (!videoService.isRoomAdmin(operatorId, req.getRoomId())) {
			log.warn("用户无权限关闭房间: uid={}, room={}", operatorId, req.getRoomId());
			return;
		}

		// 2. 关闭房间
		roomTimeoutService.cleanRoom(req.getRoomId(), operatorId, CallStatusEnum.MANAGER_CLOSE.getStatus());
	}

	private void handleKickUser(Long operatorId, WSBaseReq baseReq) {
		KickUserReq req = JSONUtil.toBean(baseReq.getData(), KickUserReq.class);

		// 1. 验证操作者权限
		if (!videoService.isRoomAdmin(operatorId, req.getRoomId())) {
			log.warn("用户无权限踢出成员: uid={}, room={}", operatorId, req.getRoomId());
			return;
		}

		// 2. 强制用户离开房间
		videoService.leaveRoom(req.getTargetUid(), req.getRoomId());

		// 3. 通知被踢用户
		notifyUserKicked(req.getRoomId(), req.getTargetUid(), operatorId, req.getReason());
	}

	private void handleMuteAll(Long operatorId, WSBaseReq baseReq) {
		MuteAllReq req = JSONUtil.toBean(baseReq.getData(), MuteAllReq.class);

		// 1. 验证操作者权限
		if (!videoService.isRoomAdmin(operatorId, req.getRoomId())) {
			log.warn("用户无权限全体静音: uid={}, room={}", operatorId, req.getRoomId());
			return;
		}

		// 2. 设置全体静音状态
		videoService.setAllMuted(req.getRoomId(), req.isMuted());

		// 3. 通知房间成员
		notifyAllMuted(req.getRoomId(), req.isMuted(), operatorId);
	}

	private void notifyUserKicked(Long roomId, Long targetUid, Long operatorId, String reason) {
		WsBaseResp<UserKickedVO> resp = new WsBaseResp<>();
		resp.setType(WSRespTypeEnum.UserKicked.getType());
		resp.setData(new UserKickedVO(roomId, targetUid, operatorId, reason));

		// 1. 通知被踢用户
		pushService.sendPushMsg(resp, Collections.singletonList(targetUid), operatorId);

		// 2. 通知其他成员
		List<Long> members = videoService.getRoomMembers(roomId);
		members.remove(targetUid);

		if (!members.isEmpty()) {
			pushService.sendPushMsg(resp, members, operatorId);
		}
	}

	private void notifyAllMuted(Long roomId, boolean muted, Long operatorId) {
		WsBaseResp<AllMutedVO> resp = new WsBaseResp<>();
		resp.setType(WSRespTypeEnum.AllMuted.getType());
		resp.setData(new AllMutedVO(roomId, muted, operatorId));

		List<Long> members = videoService.getRoomMembers(roomId);

		if (!members.isEmpty()) {
			pushService.sendPushMsg(resp, members, operatorId);
		}
	}

	// 请求VO类
	@Data
	private static class CloseRoomReq {
		private Long roomId;
	}

	@Data
	private static class KickUserReq {
		private Long roomId;
		private Long targetUid;
		private String reason;
	}

	@Data
	private static class MuteAllReq {
		private Long roomId;
		private boolean muted;
	}
}