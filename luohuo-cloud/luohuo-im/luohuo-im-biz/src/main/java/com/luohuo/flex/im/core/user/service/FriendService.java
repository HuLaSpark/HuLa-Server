package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.request.friend.FriendPermissionReq;
import com.luohuo.flex.im.domain.vo.request.friend.FriendRemarkReq;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.domain.vo.req.friend.FriendCheckReq;
import com.luohuo.flex.im.domain.vo.req.friend.FriendReq;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendCheckResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendResp;
import jakarta.validation.Valid;

import java.util.List;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author 乾乾
 */
public interface FriendService {

	void createFriend(Long roomId, Long uid, Long targetUid);

	void warmUpRoomMemberCache(List<Long> roomIdList);

    /**
     * 检查
     * 检查是否是自己好友
     *
     * @param request 请求
     * @param uid     uid
     * @return {@link FriendCheckResp}
     */
    FriendCheckResp check(Long uid, FriendCheckReq request);

	/**
	 * 建立申请
	 * @param uid 申请人
	 * @param roomId 房间id
	 * @param targetId 被申请人
	 * @param type 申请类型
	 * @return
	 */
	Long createUserApply(Long uid, Long roomId, Long targetId, String msg, Integer type, Integer channel);

	/**
	 * 与系统用户创建好友关系
	 * 与系统用户创建聊天框
	 * 系统用户发送欢迎消息
	 * 系统用户在群内发送欢迎消息
	 * @param uid 加上系统机器人的uid
	 */
	void createSystemFriend(Long uid);

    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    void deleteFriend(Long uid, Long friendUid);

	/**
	 * 联系人列表
	 * @param uid 当前登录人
	 */
    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);

	/**
	 * 修改好友备注
	 */
	Boolean updateRemark(Long employeeId, @Valid FriendRemarkReq request);

	/**
	 * 查询好友
	 */
	List<ChatMemberListResp> searchFriend(@Valid FriendReq friendReq);

	/**
	 * 设置好友权限
	 */
	Boolean permissionSettings(Long uid, FriendPermissionReq request);
}
