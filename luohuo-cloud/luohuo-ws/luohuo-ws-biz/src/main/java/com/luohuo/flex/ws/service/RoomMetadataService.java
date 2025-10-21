package com.luohuo.flex.ws.service;

import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.ws.cache.CloseRoomCacheKeyBuilder;
import com.luohuo.flex.ws.cache.RoomAdminMetadataCacheKeyBuilder;
import com.luohuo.flex.ws.cache.RoomMetadataCacheKeyBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 房间元数据服务
 *
 * 功能：
 * 1. 管理房间元数据（创建者、管理员、房间类型等）
 * 2. 维护屏幕共享状态
 * 3. 管理全体静音状态
 */
@Service
@RequiredArgsConstructor
public class RoomMetadataService {
	private final CachePlusOps cachePlusOps;

	/**
	 * 打开房间
	 * @param roomId
	 */
	public void openRoom(Long roomId) {
		cachePlusOps.set(CloseRoomCacheKeyBuilder.builder(roomId), false);
	}

	public Boolean isRoomClosed(Long roomId) {
		CacheResult<Boolean> result = cachePlusOps.get(CloseRoomCacheKeyBuilder.builder(roomId));
		return result.isNull() || result.isNullVal()? true: result.getRawValue();
	}

	/**
	 * 关闭房间清除key
	 * @param roomId
	 */
	public void markRoomClosed(Long roomId) {
		setRoomMetadata(roomId, "startTime", null);
		cachePlusOps.del(CloseRoomCacheKeyBuilder.builder(roomId));
	}

	/**
	 * 设置房间元数据字段
	 */
	public void setRoomMetadata(Long roomId, String field, Object value) {
		cachePlusOps.hSet(RoomMetadataCacheKeyBuilder.builder(roomId, field), value);
	}

	/**
	 * 获取房间元数据字段
	 * 当 field = roomType时，返回 [group、p2p]
	 */
	public <T> T getRoomMetadata(Long roomId, String field) {
		CacheResult<T> result = cachePlusOps.hGet(RoomMetadataCacheKeyBuilder.builder(roomId, field));
		return result.getValue() == null ? null : result.getValue();
	}

	/**
	 * 获取房间创建者
	 */
	public Long getRoomCreator(Long roomId) {
		return getRoomMetadata(roomId, "creator");
	}

	/**
	 * 设置房间创建者
	 */
	public void setRoomCreator(Long roomId, Long creatorUid) {
		setRoomMetadata(roomId, "creator", creatorUid);
	}

	/**
	 * 获取房间接通电话时间
	 */
	public Long getRoomStartTime(Long roomId) {
		return getRoomMetadata(roomId, "startTime");
	}

	/**
	 * 设置房间接通电话时间
	 */
	public void setRoomStartTime(Long roomId) {
		setRoomMetadata(roomId, "startTime", TimeUtils.getTime());
	}

	/**
	 * 添加房间管理员, 需要联动群主、群管理的数据
	 */
	public void addRoomAdmin(Long roomId, Long adminUid) {
		cachePlusOps.sAdd(RoomAdminMetadataCacheKeyBuilder.builder(roomId), adminUid);
	}

	/**
	 * 获取房间媒体类型
	 */
	public Boolean getRoomMediumType(Long roomId) {
		return getRoomMetadata(roomId, "mediumType");
	}

	/**
	 * 设置房间媒体类型
	 * @param roomId 房间号
	 * @param isVideo true = 视频通话 false = 语音通话
	 */
	public void setRoomMediumType(Long roomId, Boolean isVideo) {
		setRoomMetadata(roomId, "mediumType", isVideo);
	}

	/**
	 * 设置租户id
	 * @param roomId 房间ID
	 * @param tenantId 租户id
	 */
	public void setTenantId(Long roomId, Long tenantId) {
		setRoomMetadata(roomId, "tenantId", tenantId);
	}

	/**
	 * 获取租户id
	 */
	public Long getTenantId(Long roomId) {
		return getRoomMetadata(roomId, "tenantId");
	}

	/**
	 * 设置房间类型
	 * @param roomId 房间ID
	 * @param type 是否为群组房间
	 */
	public void setRoomType(Long roomId, Integer type) {
		setRoomMetadata(roomId, "type", type);
	}

	public Integer getRoomType(Long roomId) {
		return getRoomMetadata(roomId, "type");
	}

	/**
	 * 获取房间管理员列表
	 */
	public Set<Long> getRoomAdmins(Long roomId) {
		return cachePlusOps.sMembers(RoomAdminMetadataCacheKeyBuilder.builder(roomId)).stream()
				.map(obj -> Long.parseLong(obj.toString()))
				.collect(Collectors.toSet());
	}

	/**
	 * 检查用户是否是房间管理员
	 */
	public boolean isRoomAdmin(Long roomId, Long uid) {
		// 创建者也是管理员
		Long creator = getRoomCreator(roomId);
		if (creator != null && creator.equals(uid)) {
			return true;
		}
		// 检查管理员列表
		Set<Long> admins = getRoomAdmins(roomId);
		return admins != null && admins.contains(uid);
	}

	/**
	 * 设置全体静音状态
	 */
	public void setAllMuted(Long roomId, boolean muted) {
		setRoomMetadata(roomId, "allMuted", muted);
	}

	/**
	 * 获取全体静音状态
	 */
	public boolean isAllMuted(Long roomId) {
		Boolean muted = getRoomMetadata(roomId, "allMuted");
		return muted != null && muted;
	}

	/**
	 * 设置屏幕共享状态
	 */
	public void setScreenSharing(Long roomId, Long userId, boolean sharing) {
		if (sharing) {
			setRoomMetadata(roomId, "screenSharingUser", userId);
		} else {
			// 清除共享状态
			cachePlusOps.hDel(RoomMetadataCacheKeyBuilder.builder(roomId, "screenSharingUser"));
		}
	}

	/**
	 * 获取当前屏幕共享用户ID
	 */
	public Long getScreenSharingUser(Long roomId) {
		return getRoomMetadata(roomId, "screenSharingUser");
	}

	/**
	 * 检查是否正在屏幕共享
	 */
	public boolean isScreenSharing(Long roomId) {
		return getScreenSharingUser(roomId) != null;
	}

	/**
	 * 判断房间是否为群组房间
	 * @param roomId 房间ID
	 * @return true=群组房间, false=单聊房间
	 */
	public boolean isGroupRoom(Long roomId) {
		String type = getRoomMetadata(roomId, "type");
		return "group".equals(type);
	}
}