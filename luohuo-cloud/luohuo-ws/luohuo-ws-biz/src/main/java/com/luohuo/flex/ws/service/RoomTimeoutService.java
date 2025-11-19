package com.luohuo.flex.ws.service;

import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.enums.CallStatusEnum;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.model.ws.CallEndReq;
import com.luohuo.flex.ws.ReactiveContextUtil;
import com.luohuo.flex.ws.vo.CallTimeoutVO;
import com.luohuo.flex.ws.vo.RoomClosedVO;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 房间超时管理服务
 *
 * 功能：
 * 1. 设置和管理房间超时任务
 * 2. 刷新房间活跃时间
 * 3. 清理空闲房间
 * 4. 处理呼叫超时
 *
 * 关键机制：
 * - 空闲房间超时自动清理（默认5分钟）
 * - 呼叫超时处理（30秒无应答）
 * - 房间关闭通知
 */
@Order(14)
@Service
@RequiredArgsConstructor
public class RoomTimeoutService {
    private final VideoChatService videoService;
	private final PushService pushService;
	private final RocketMQTemplate rocketMQTemplate;
	private final RoomMetadataService roomMetadataService;
    private final ScheduledExecutorService scheduler;
    
    // 房间ID -> 超时任务
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();

	/**
	 * 获取房间接通电话时间
	 */
	public Long getRoomStartTime(Long roomId) {
		return roomMetadataService.getRoomStartTime(roomId);
	}

	public Boolean isClose(Long roomId) {
		return roomMetadataService.isRoomClosed(roomId);
	}
    /**
     * 设置房间超时、无成员时自动清理
     * @param roomId 房间ID
     * @param timeoutSeconds 超时时间（秒）
     */
	@RedissonLock(key = "#roomId", waitTime = 500, unit = TimeUnit.MILLISECONDS)
    public void scheduleRoomCleanup(Long roomId, long timeoutSeconds) {
        // 取消现有任务
        cancelTimeoutTask(roomId);
        
        // 创建新任务
        ScheduledFuture<?> future = scheduler.schedule(() -> {
			// 二次状态校验、60秒延迟内用户重新加入
			if (!roomMetadataService.isRoomClosed(roomId) && videoService.getRoomMembers(roomId).isEmpty()) {
				cleanRoom(roomId, null, CallStatusEnum.TIMEOUT.getStatus());
			}
        }, timeoutSeconds, TimeUnit.SECONDS);
        
        timeoutTasks.put(roomId, future);
    }
    
    /**
     * 刷新房间活跃时间，5分钟无活动自动清理
     * @param roomId 房间ID
     */
    public void refreshRoomActivity(Long roomId) {
        scheduleRoomCleanup(roomId, 300);
    }
    
    /**
     * 取消房间超时任务
     * @param roomId 房间ID
     */
    public void cancelTimeoutTask(Long roomId) {
		if (roomId == null) return;

		ScheduledFuture<?> task = timeoutTasks.remove(roomId);
		if (task != null) task.cancel(false);
    }

	/**
	 * 注入房间元数据
	 * @param room 房间数据
	 * @param uid 创建人
	 */
	public void setRoomMeta(Room room, Long uid, boolean isVideo) {
		Long roomId = room.getId();

		roomMetadataService.openRoom(roomId);
		roomMetadataService.setTenantId(roomId, room.getTenantId());
		roomMetadataService.setRoomMediumType(roomId, isVideo);
		roomMetadataService.setRoomType(roomId, room.getType());
		roomMetadataService.setRoomCreator(roomId, uid);
		roomMetadataService.addRoomAdmin(roomId, uid);

		// 群聊时
		if(room.getType().equals(1) && isVideo){
			senStartMsg(uid, roomId, CallStatusEnum.ONGOING.getStatus());
		}
	}

	/**
	 * 初始化房间接通时间
	 * @param roomId
	 */
	public void setRoomStartTime(Long roomId) {
		roomMetadataService.setRoomStartTime(roomId);
	}
    
    /**
     * 清理房间资源
     * @param roomId 房间ID
     */
	@RedissonLock(prefixKey = "cleanRoom:", key = "#roomId")
    public void cleanRoom(Long roomId, Long uid, String reason) {
		// 1. 检查房间是否已关闭
		if (roomMetadataService.isRoomClosed(roomId)) return;

		// 2. 取消所有相关超时任务
		cancelTimeoutTask(roomId);

		// 3. 发送音视频消息到房间
		senMsg(roomId, uid, reason);

		// 5. 标记关闭
		roomMetadataService.markRoomClosed(roomId);

		// 6. 获取房间所有成员
		List<Long> members = videoService.getRoomMembers(roomId);

		// 7. 清理房间数据
		videoService.cleanRoomData(roomId);

		// 8. 发送房间关闭通知
		if(!members.isEmpty()){
			WsBaseResp<RoomClosedVO> resp = new WsBaseResp<>();
			resp.setType(WSRespTypeEnum.RoomClosed.getType());
			resp.setData(new RoomClosedVO(roomId, reason));

			pushService.sendPushMsg(resp, members, 0L);
		}
    }

	/**
	 * 发送音视频消息
	 * @param roomId 房间id
	 * @param uid 操作人
	 * @param reason 结束原因
	 */
	private void senMsg(Long roomId, Long uid, String reason) {
		// 获取房间接通电话的时间
		Long startTime = roomMetadataService.getRoomStartTime(roomId);
		// 获取打电话的人
		Long creator = roomMetadataService.getRoomCreator(roomId);
		// 获取租户id
		Long tenantId = roomMetadataService.getTenantId(roomId);
		// 获取房间类型
		Integer type = roomMetadataService.getRoomType(roomId);
		// 获取房间媒体类型
		Boolean mediumType = roomMetadataService.getRoomMediumType(roomId);
		if(ReactiveContextUtil.getTenantId() == null){
			// 给自动关闭的线程赋值当前打电话人的数据
			ReactiveContextUtil.setTenantId(tenantId);
			ReactiveContextUtil.setUid(creator);
		}

		try {
			rocketMQTemplate.send(MqConstant.FRONTEND_MSG_INPUT_TOPIC, MessageBuilder.withPayload(new CallEndReq(uid, roomId, tenantId, type.equals(1), mediumType, creator, startTime, TimeUtils.getTime(), reason)).build());
		} finally {
			ReactiveContextUtil.remove();
		}

	}

	/**
	 * 群聊时立马发送群聊通话消息
	 * @param uid 操作人
	 * @param roomId 房间id
	 * @param reason
	 */
	private void senStartMsg(Long uid, Long roomId, String reason) {
		// 获取打电话的人
		Long creator = roomMetadataService.getRoomCreator(roomId);
		// 获取租户id
		Long tenantId = roomMetadataService.getTenantId(roomId);
		rocketMQTemplate.send(MqConstant.FRONTEND_MSG_INPUT_TOPIC, MessageBuilder.withPayload(new CallEndReq(uid, creator, roomId, tenantId, TimeUtils.getTime(), reason)).build());
	}

	/**
	 * 通知呼叫超时
	 */
	private void notifyCallTimeout(Long callerUid, Long targetUid) {
		WsBaseResp<CallTimeoutVO> resp = new WsBaseResp<>();
		resp.setType(WSRespTypeEnum.TIMEOUT.getType());
		resp.setData(new CallTimeoutVO(targetUid));

		// 1. 推送超时消息呼叫方
		pushService.sendPushMsg(resp, callerUid, targetUid);
	}

	/**
	 * 设置呼叫超时（30秒无应答）
	 */
	public void scheduleCallTimeout(Long caller, Long receiver, Long roomId) {
		// 取消现有任务
		cancelTimeoutTask(roomId);

		ScheduledFuture<?> future = scheduler.schedule(() -> {
			// 检查房间是否已关闭
			if (roomMetadataService.isRoomClosed(roomId)) {
				return;
			}

			// 通知主叫方呼叫超时
			notifyCallTimeout(caller, receiver);

			// 清理房间、通知超时
			cleanRoom(roomId, null, CallStatusEnum.TIMEOUT.getStatus());
		}, 30, TimeUnit.SECONDS);

		timeoutTasks.put(roomId, future);
	}

	@PreDestroy
	public void shutdown() {
		// 1. 取消所有待处理任务
		timeoutTasks.values().forEach(future -> future.cancel(false));
		timeoutTasks.clear();

		// 2. 优雅关闭线程池
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}