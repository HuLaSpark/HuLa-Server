package com.luohuo.flex.ws.websocket.processor.meet;

import cn.hutool.json.JSONUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.enums.CallResponseStatus;
import com.luohuo.flex.model.enums.CallStatusEnum;
import com.luohuo.flex.model.ws.WSBaseReq;
import com.luohuo.flex.model.enums.WSReqTypeEnum;
import com.luohuo.flex.ws.service.PushService;
import com.luohuo.flex.ws.service.RoomTimeoutService;
import com.luohuo.flex.ws.service.VideoChatService;
import com.luohuo.flex.ws.vo.CallAcceptedVO;
import com.luohuo.flex.ws.vo.CallRejectedVO;
import com.luohuo.flex.ws.vo.CallReqVO;
import com.luohuo.flex.ws.vo.CallRequestVO;
import com.luohuo.flex.ws.vo.CallResponseVO;
import com.luohuo.flex.ws.websocket.processor.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;

import static com.luohuo.flex.model.enums.WSReqTypeEnum.VIDEO_CALL_REQUEST;
import static com.luohuo.flex.model.enums.WSReqTypeEnum.VIDEO_CALL_RESPONSE;

/**
 * 视频呼叫处理器
 *
 * 功能：
 * 1. 处理视频呼叫请求
 * 2. 处理呼叫响应（接受/拒绝）
 * 3. 管理呼叫超时
 * 4. 通知呼叫结果
 *
 * 支持的消息类型：
 * - VIDEO_CALL_REQUEST：视频呼叫请求
 * - VIDEO_CALL_RESPONSE：呼叫响应
 */
@Slf4j
@Order(11)
@Component
@RequiredArgsConstructor
public class VideoCallProcessor implements MessageProcessor {
    private final VideoChatService videoService;
	private final PushService pushService;
	private final RoomTimeoutService roomTimeoutService;

    @Override
    public boolean supports(WSBaseReq req) {
        return VIDEO_CALL_REQUEST.eq(req.getType()) ||
               VIDEO_CALL_RESPONSE.eq(req.getType());
    }

    @Override
    public void process(WebSocketSession session, Long uid, WSBaseReq baseReq) {
        switch (WSReqTypeEnum.of(baseReq.getType())) {
            case VIDEO_CALL_REQUEST -> handleCallRequest(uid, baseReq);
            case VIDEO_CALL_RESPONSE -> handleCallResponse(uid, baseReq);
        }
    }

	/**
	 * 发起通话请求
	 * @param callerUid 发起人
	 * @param baseReq 接收人
	 */
	private void handleCallRequest(Long callerUid, WSBaseReq baseReq) {
        CallRequestVO request = JSONUtil.toBean(baseReq.getData(), CallRequestVO.class);
		Room room = videoService.getRoomMetadata(request.getRoomId());
		if (room == null) {
			log.error("房间不存在: room");
			throw new BizException("房间不存在");
		}

		// 1. 主叫方加入房间
		List<Long> pushIds = videoService.joinRoom(callerUid, room);

		// 2. 注入房间元数据
		roomTimeoutService.setRoomMeta(room, callerUid, request.isVideo());

		// 2. 发送呼叫请求给被叫方
        WsBaseResp<CallReqVO> resp = new WsBaseResp<>();
        resp.setType(WSRespTypeEnum.VideoCallRequest.getType());
		resp.setData(new CallReqVO(request.getRoomId(), callerUid, request.getTargetUid(), request.isVideo()));

		pushService.sendPushMsg(resp, pushIds, callerUid);

		// 3. 设置呼叫超时（30秒）
		roomTimeoutService.scheduleCallTimeout(callerUid, request.getTargetUid(), room.getId());
    }

	/**
	 * 双方都有可能调用此方法, 因此uid可能是主叫方、被叫方
	 * @param uid 主叫方、被叫方
	 * @param baseReq
	 */
	private void handleCallResponse(Long uid, WSBaseReq baseReq) {
        CallResponseVO response = JSONUtil.toBean(baseReq.getData(), CallResponseVO.class);
        // 1. 解析房间ID
        Long roomId = response.getRoomId();

		switch (CallResponseStatus.of(response.getAccepted())) {
			case ACCEPTED -> {
				// 2. 立即取消超时任务
				roomTimeoutService.cancelTimeoutTask(roomId);

				// 3. 自己加入房间即可  type = 2表示P2P
				videoService.joinRoom(uid, videoService.getRoomMetadata(roomId));

				// 5. 通知双方呼叫已接受
				notifyCallAccepted(response.getCallerUid(), uid, roomId);

				// 6. 设置房间接通时间
				roomTimeoutService.setRoomStartTime(roomId);

				// 7. 主叫方开始发送信令
				// notifyStartSignaling(response.getCallerUid(), roomId);
			}
			case TIMEOUT, REJECTED, HANGUP -> {
				// 8. 通知主叫方呼叫被拒绝、超时
				if(roomTimeoutService.isClose(roomId)){
					return;
				}
				roomTimeoutService.cancelTimeoutTask(roomId);
				CallStatusEnum callStatusEnum = notifyCallRejected(roomId, response.getCallerUid(), uid, response.getAccepted());
				roomTimeoutService.cleanRoom(roomId, uid, callStatusEnum.getStatus());
			}
		}
    }

//	private void notifyStartSignaling(Long responderUid, Long roomId) {
//		WsBaseResp<StartSignalingVO> resp = new WsBaseResp<>();
//		resp.setType(WSRespTypeEnum.StartSignaling.getType());
//		resp.setData(new StartSignalingVO(roomId));
//
//		rocketMQTemplate.send(MqConstant.PUSH_TOPIC, MessageBuilder
//				.withPayload(new PushMessageDTO(resp, Collections.singletonList(responderUid),
//						WSPushTypeEnum.USER.getType(), null))
//				.setHeader("KEYS", "start_signaling_" + roomId)
//				.build());
//	}

	/**
	 * 呼叫接通的逻辑
	 * @param callerUid 呼叫方
	 * @param uid 接收方、当前登录人
	 * @param roomId 房间id
	 */
	private void notifyCallAccepted(Long callerUid, Long uid, Long roomId) {
		// 通知主叫方呼叫已接受
		WsBaseResp<CallAcceptedVO> respToCaller = new WsBaseResp<>();
		respToCaller.setType(WSRespTypeEnum.CallAccepted.getType());
		respToCaller.setData(new CallAcceptedVO(uid, roomId));

		// 通知被叫方呼叫已接受
		WsBaseResp<CallAcceptedVO> respToResponder = new WsBaseResp<>();
		respToResponder.setType(WSRespTypeEnum.CallAccepted.getType());
		respToResponder.setData(new CallAcceptedVO(callerUid, roomId));

		pushService.sendPushMsg(respToCaller, Collections.singletonList(callerUid), uid);
		pushService.sendPushMsg(respToResponder, Collections.singletonList(uid), uid);
	}

	/**
	 * 告知前端对方已经拒绝、通话超时
	 * @param opposite 对方
	 * @param uid 接收方、登录人
	 */
	private CallStatusEnum notifyCallRejected(Long roomId, Long opposite, Long uid, Integer accepted) {
		WSRespTypeEnum anEnum;
		CallStatusEnum callStatusEnum;
		switch (CallResponseStatus.of(accepted)) {
			case REJECTED -> {
				anEnum = WSRespTypeEnum.CallRejected;
				callStatusEnum = CallStatusEnum.REJECTED;
			}
			case HANGUP -> {
				// 获取房间接通电话的时间
				Long roomStartTime = roomTimeoutService.getRoomStartTime(roomId);
				if (roomStartTime == null){
					anEnum = WSRespTypeEnum.CANCEL;
					callStatusEnum = CallStatusEnum.CANCEL;
				} else {
					anEnum = WSRespTypeEnum.DROPPED;
					callStatusEnum = CallStatusEnum.DROPPED;
				}
			}
			default -> {
				anEnum = WSRespTypeEnum.TIMEOUT;
				callStatusEnum = CallStatusEnum.TIMEOUT;
			}
		}

		WsBaseResp<CallRejectedVO> resp = new WsBaseResp<>();
		resp.setType(anEnum.getType());
		resp.setData(new CallRejectedVO(uid));
		pushService.sendPushMsg(resp, Collections.singletonList(opposite), opposite);
		return callStatusEnum;
	}
}