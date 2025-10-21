package com.luohuo.flex.ws.service;

import cn.hutool.extra.spring.SpringUtil;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.common.OnlineService;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.ws.cache.UserRoomsCacheKeyBuilder;
import com.luohuo.flex.ws.cache.VideoRoomsCacheKeyBuilder;
import com.luohuo.flex.ws.vo.MediaControlVO;
import com.luohuo.flex.ws.vo.UserJoinRoomVO;
import com.luohuo.flex.ws.vo.VideoSignalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
/**
 * 视频聊天核心服务
 *
 * 功能：
 * 1. 管理用户与房间的关系
 * 2. 管理房间与成员的关系
 * 3. 处理信令转发
 * 4. 创建和管理群组视频房间
 * 5. 清理房间数据
 *
 * 关键特性：
 * - 所有房间操作都通过分布式锁保证原子性
 * - 支持点对点和群组视频通话
 * - 集成消息队列进行通知推送
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoChatService {
	private final OnlineService onlineService;
	private final CachePlusOps cachePlusOps;
	private final PushService pushService;
	private final RoomMetadataService roomMetadataService;

	public Room getRoomMetadata(Long roomId) {
		List<Room> rooms = cachePlusOps.mGet(Collections.singletonList("luohuo:roomInfo:roomId_" + roomId), Room.class);
		return rooms.isEmpty() ? null : rooms.get(0);
	}

    /**
     * 用户加入视频房间, 校验用户是否在群聊有权限
	 * @param uid 加入房间的用户
	 * @param room 房间属性
	 * return 返回已经推送的人
     */
	@RedissonLock(key = "#room.id", prefixKey = "lock:room:join", waitTime = 500, unit = TimeUnit.MILLISECONDS)
    public List<Long> joinRoom(Long uid, Room room) {
		// 1. 处理房间类型
		Integer type = room.getType(); // type 房间类型 1 -> 群聊 2 -> 单聊
		UserJoinRoomVO resp = new UserJoinRoomVO(uid, room.getId());
		if (type == 1) {
			// TODO 非群成员无法加入

			// 显示群名称和头像
			resp.setName("");
			resp.setAvatar("");
		} else if (type == 2) {
			// TODO 大于2人，私聊房间已满
			// 显示对方用户信息
//			resp.setName(userService.getUserInfo(excludeUid));
//			resp.setAvatar(userService.getUserInfo(excludeUid));
		}

		// 2. 幂等性检查：如果用户已在房间中，直接返回
//		if (isUserInRoom(uid, roomId)) {
//			log.debug("用户已在房间中: uid={}, room={}", uid, roomId);
//			throw new BizException("用户已在房间中");
//		}

        // 3. 记录用户所在的房间
		cachePlusOps.sAdd(UserRoomsCacheKeyBuilder.build(uid), room.getId());

        // 5. 记录房间中的用户
		cachePlusOps.sAdd(VideoRoomsCacheKeyBuilder.build(room.getId()), uid);

		// 6. 刷新房间活跃时间
		SpringUtil.getBean(RoomTimeoutService.class).refreshRoomActivity(room.getId());

        // 7. 通知房间内其他用户
		List<Long> pushUids = notifyRoomMembers(room.getId(), uid, WSRespTypeEnum.JoinVideo, resp);

		log.info("用户加入房间: uid={}, room={}, type={}", uid, room.getId(), room.getType() == 1 ? "群聊" : "私聊");

		return pushUids;
    }

	/**
	 * 用户离开视频房间，私聊时是挂断，群聊是单个离开
	 * @param uid 离开房间的用户
	 * @param roomId 房间id
	 */
	@RedissonLock(key = "#roomId", prefixKey = "lock:room:leave", waitTime = 500, unit = TimeUnit.MILLISECONDS)
	public void leaveRoom(Long uid, Long roomId) {
		if (roomMetadataService.isRoomClosed(roomId)) {
			return; // 房间已关闭，无需操作
		}

		// 1. 从用户房间列表中移除
		if (!isUserInRoom(uid, roomId)) {
			return;
		}

		// 2. 从用户房间列表中移除
		cachePlusOps.sRem(UserRoomsCacheKeyBuilder.build(uid), roomId);

		// 3. 从房间用户列表中移除
		cachePlusOps.sRem(VideoRoomsCacheKeyBuilder.build(roomId), uid);

		// 5. 通知房间内其他用户
		notifyRoomMembers(roomId, uid, WSRespTypeEnum.LeaveVideo, new UserJoinRoomVO(uid, roomId));

		// 6. 如果房间为空，触发清理
		if (getRoomMembers(roomId).isEmpty()) {
			if (!roomMetadataService.isRoomClosed(roomId)) {
				SpringUtil.getBean(RoomTimeoutService.class).scheduleRoomCleanup(roomId, 60);
			}
		}
	}

	/**
	 * waitTime = 100 信令转发需要更低延迟
	 * 转发视频信令给房间内其他用户：注意! 这里必须转发给所有在线的人员，包含已经加入视频的人员
	 * 在线成员未加入会议的显示：一键加入，正在进行通话等按钮
	 * 在线且加入会议的显示：信令此次转发内容
	 * @param senderUid 发送者用户ID
	 * @param roomId 房间ID
	 * @param signal 视频信令内容
	 * @param signalType VideoSignal=视频 AudioSignal=语音
	 */
	@RedissonLock(key = "#roomId", prefixKey = "lock:room:signal", waitTime = 100, unit = TimeUnit.MILLISECONDS)
	public void forwardSignal(Long senderUid, Long roomId, String signal, String signalType) {
		// 1. 获取房间内其他成员
		try {
			List<Long> uidList = getUserList(roomId);
			uidList.remove(senderUid);

			if (uidList.isEmpty()) return;

			// 2. 构造信令消息
			WsBaseResp<VideoSignalVO> resp = new WsBaseResp<>();
			resp.setType(WSRespTypeEnum.WEBRTC_SIGNAL.getType());
			resp.setData(new VideoSignalVO(senderUid, roomId, signalType, signal));

			// 3. 批量推送
			pushService.sendPushMsg(resp, uidList, senderUid);
		} catch (Exception e) {
			// 在锁外刷新，避免锁竞争
			SpringUtil.getBean(RoomTimeoutService.class).refreshRoomActivity(roomId);
		}
	}

	/**
	 * 获取房间内所有人员id
	 * @param roomId 房间id
	 */
	public List<Long> getUserList(Long roomId) {
		return onlineService.getGroupMembers(roomId);
	}

	/**
	 * 转发媒体控制信号
	 */
	public void forwardControlSignal(Long senderUid, Long roomId, WsBaseResp<MediaControlVO> controlResp) {
		// 1. 获取房间内其他成员
		List<Long> members = getRoomMembers(roomId);
		members.remove(senderUid);

		if (members.isEmpty()) return;

		// 2. 批量推送
		pushService.sendPushMsg(controlResp, members, senderUid);
	}

	/**
	 * 创建群视频房间
	 * @param roomId 房间ID
	 * @param creatorUid 创建者用户ID
	 * @return 房间ID
	 */
	@RedissonLock(key = "#roomId", prefixKey = "lock:group:create", waitTime = 1000, unit = TimeUnit.MILLISECONDS)
	public Long createGroupRoom(Long roomId, Long creatorUid) {
		Room room = getRoomMetadata(roomId);
		if (room == null) throw new BizException("房间不存在");

		// 初始化房间
		cachePlusOps.sAdd(VideoRoomsCacheKeyBuilder.build(roomId), creatorUid);
		cachePlusOps.sAdd(UserRoomsCacheKeyBuilder.build(creatorUid), roomId);

		// 设置房间元数据 房间类型、设置关联群组ID、创建人、开始打电话的时间
//		roomMetadataService.setRoomStartTime(roomId);
//		roomMetadataService.setRoomType(roomId, 1);
//		roomMetadataService.setRoomCreator(roomId, creatorUid);
//		roomMetadataService.addRoomAdmin(roomId, creatorUid);
		return roomId;
	}

	/**
	 * 清理房间数据 [仅仅关闭房间时内部调用]
	 */
	@RedissonLock(prefixKey = "cleanRoomData:", key = "#roomId")
	public void cleanRoomData(Long roomId) {
		// 1. 获取房间所有成员
		List<Long> members = getRoomMembers(roomId);

		// 2. 从所有成员的房间列表中移除该房间
		members.forEach(uid -> cachePlusOps.sRem(UserRoomsCacheKeyBuilder.build(uid), roomId));

		// 3. 删除房间成员集合 [后续可以改为管道删除]
		cachePlusOps.del(VideoRoomsCacheKeyBuilder.build(roomId));
	}

	/**
	 * 获取用户加入的所有视频房间 [后台管理中可以调用]
	 * @param uid 用户ID
	 * @return 房间ID列表
	 */
	public Set<Long> getUserRooms(Long uid) {
		return cachePlusOps.sMembers(UserRoomsCacheKeyBuilder.build(uid)).stream()
				.map(obj -> {
					if (obj instanceof Long) {
						return (Long) obj;
					}
					return null;
				})
				.filter(obj -> obj != null)
				.collect(Collectors.toSet());
	}

	/**
	 * 获取视频房间内所有成员
	 * @param roomId 房间ID
	 * @return 成员用户ID列表
	 */
	public List<Long> getRoomMembers(Long roomId) {
		return cachePlusOps.sMembers(VideoRoomsCacheKeyBuilder.build(roomId)).stream()
				.map(obj -> {
					if (obj instanceof Long) {
						return (Long) obj;
					}
					return null;
				})
				.filter(obj -> obj != null).distinct()
				.collect(Collectors.toList());
	}

	/**
	 * 检查用户是否在房间中
	 * @param uid 用户ID
	 * @param roomId 房间ID
	 * @return 是否在房间中
	 */
	public boolean isUserInRoom(Long uid, Long roomId) {
		return cachePlusOps.sIsMember(VideoRoomsCacheKeyBuilder.build(roomId), uid);
	}

	/**
	 * 通知房间内其他成员
	 * @param roomId 房间ID
	 * @param excludeUid 要排除的用户ID
	 * @param respType 响应类型
	 * @param data 通知数据
	 */
	private <T> List<Long> notifyRoomMembers(Long roomId, Long excludeUid, WSRespTypeEnum respType, T data) {
		// 1. 获取房间内所有成员
		List<Long> uidList = getUserList(roomId);
		if (uidList.isEmpty()) return new ArrayList<>();

		// 2. 排除当前用户
		List<Long> pushUids = uidList.stream()
				.filter(uid -> !uid.equals(excludeUid))
				.collect(Collectors.toList());

		if (pushUids.isEmpty()) return new ArrayList<>();

		// 3. 构造通知消息
		WsBaseResp<T> resp = new WsBaseResp<>();
		resp.setType(respType.getType());
		resp.setData(data);

		// 4. 批量推送
		pushService.sendPushMsg(resp, pushUids, excludeUid);
		return pushUids;
	}

	/**
	 * 检查用户是否是房间创建者或群管理员
	 */
	public boolean isRoomAdmin(Long uid, Long roomId) {
		return roomMetadataService.isRoomAdmin(uid, roomId);
	}

	/**
	 * 设置全体静音状态\更新房间元数据
	 */
	public void setAllMuted(Long roomId, boolean muted) {
		roomMetadataService.setAllMuted(roomId, muted);
	}

	/**
	 * 设置屏幕共享状态\更新房间元数据
	 */
	public void setScreenSharing(Long roomId, Long userId, boolean sharing) {
		roomMetadataService.setScreenSharing(roomId, userId, sharing);
	}

	/**
	 * 保存网络质量数据、存储到数据库或缓存
	 */
	public void saveNetworkQuality(Long uid, Long roomId, double quality) {

		log.info("存储网络质量数据: uid={}, room={}, quality={}", uid, roomId, quality);
	}

	/**
	 * 获取房间管理员列表、返回房间创建者和指定管理员
	 */
	public List<Long> getRoomAdmins(Long roomId) {
		return Collections.singletonList(roomMetadataService.getRoomCreator(roomId));
	}

}