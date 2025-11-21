package com.luohuo.flex.im.core.chat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.flex.im.domain.entity.Announcements;
import com.luohuo.flex.im.domain.entity.AnnouncementsReadRecord;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.domain.entity.RoomFriend;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.vo.req.room.GroupPageReq;
import com.luohuo.flex.im.domain.vo.request.GroupAddReq;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.im.domain.vo.response.AnnouncementsResp;
import com.luohuo.flex.im.domain.vo.response.MemberResp;

import java.util.List;

/**
 * 房间底层管理
 * @author nyh
 */
public interface RoomService {

	/**
	 * 获取房间
	 */
	Room getById(Long roomId);

	/**
	 * 移除房间
	 */
	boolean removeById(Long roomId);

    /**
     * 创建一个单聊房间
     */
    RoomFriend createFriendRoom(List<Long> uidList);

    RoomFriend getFriendRoom(Long uid1, Long uid2);

    /**
     * 禁用一个单聊房间 [目前是谁删除的，谁就没有会话]
     */
    void disableFriendRoom(Long roomId, Long uid, Long friendUid);

    /**
     * 创建一个群聊房间
     */
    RoomGroup createGroupRoom(Long uid, GroupAddReq groupAddReq);

	/**
	 * 群聊列表
	 * @param uid 登录用户id
	 */
	List<MemberResp> groupList(Long uid);

	/**
	 * 获取所有群聊列表（不需要权限）
	 */
	List<MemberResp> getAllGroupList();

	/**
	 * 分页查询所有群聊列表（支持按群昵称和群成员昵称搜索）
	 */
	PageBaseResp<MemberResp> getGroupPage(GroupPageReq req);

    /**
     * 校验当前用户是否在群里
     * @param uid 当前用户
     * @param roomId 房间id
     */
    void checkUser(Long uid, Long roomId);

	/**
	 * 修改房间信息
	 */
	Boolean updateRoomInfo(RoomGroup roomGroup);

	/**
	 * isSpecial：true -> 获取群主、管理员
	 * @return
	 */
	List<Long> getGroupUsers(Long id, Boolean isSpecial);

	/**
	 * 读公告
	 *
	 * @param uid
	 * @param announcementId
	 * @return
	 */
	Boolean readAnnouncement(Long uid, Long announcementId);

	/**
	 * 查询公告列表
	 */
	IPage<Announcements> announcementList(Long roomId, IPage<Announcements> page);

	/**
	 * 查询公告
	 */
	AnnouncementsResp getAnnouncement(Long id);

	/**
	 * 查询公告已读数量
	 */
	Long getAnnouncementReadCount(Long announcementId);

	/**
	 * 删除指定公告
	 */
	Boolean announcementDelete(Long id);

	/**
	 * 保存群公告
	 */
	Boolean saveAnnouncements(Announcements announcements);

	/**
	 * 批量保存公告已读记录
	 */
	Boolean saveBatchAnnouncementsRecord(List<AnnouncementsReadRecord> announcementsReadRecordList);

	/**
	 * 改变好友的消息屏蔽类型
	 */
	void updateState(Boolean u1, Long uid1, Long uid2, Boolean deFriend);

	/**
	 * 创建群成员
	 */
	void createGroupMember(Long groupId, Long uid);

	/**
	 * 编辑公告
	 */
	Boolean updateAnnouncement(Announcements announcement);
}
