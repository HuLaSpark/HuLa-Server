package com.luohuo.flex.ws.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 会话恢复服务
 *
 * 功能：
 * 1. 在用户重连时恢复其视频会话状态
 * 2. 重新加入用户之前所在的视频房间
 * 3. 确保用户状态一致性
 *
 * 使用场景：
 * - 用户网络断开后重新连接
 * - 用户切换设备后重新登录
 * - 服务端重启后恢复用户状态
 *
 * 工作流程：获取用户之前加入的所有房间 -> 根据房间元数据判断房间类型 -> 重新加入所有房间
 */
@Service
@RequiredArgsConstructor
public class SessionRecoveryService {
    private final VideoChatService videoService;
	private final RoomMetadataService roomMetadataService;
    
    public void recoverUserSessions(Long uid) {
        // 获取用户所有房间
        Set<Long> rooms = videoService.getUserRooms(uid);
        // 重新加入所有房间
        rooms.forEach(roomId -> videoService.joinRoom(uid, videoService.getRoomMetadata(roomId)));
    }
}