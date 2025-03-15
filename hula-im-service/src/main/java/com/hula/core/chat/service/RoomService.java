package com.hula.core.chat.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.common.domain.po.RoomChatInfoPO;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.core.chat.domain.entity.Announcements;
import com.hula.core.chat.domain.entity.AnnouncementsReadRecord;
import com.hula.core.chat.domain.entity.RoomFriend;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.vo.request.GroupAddReq;
import com.hula.core.chat.domain.vo.request.room.AnnouncementsParam;
import com.hula.core.chat.domain.vo.response.AnnouncementsResp;

import java.util.List;

/**
 * 房间底层管理
 * @author nyh
 */
public interface RoomService {

    /**
     * 创建一个单聊房间
     */
    RoomFriend createFriendRoom(List<Long> uidList);

    RoomFriend getFriendRoom(Long uid1, Long uid2);

    /**
     * 禁用一个单聊房间
     */
    void disableFriendRoom(List<Long> uidList);


    /**
     * 创建一个群聊房间
     */
    RoomGroup createGroupRoom(Long uid, GroupAddReq groupAddReq);


    void groupList(Long uid, IPage<GroupListVO> page);

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
	 * 查询公告
	 */
	AnnouncementsResp getAnnouncement(Long id);

	/**
	 * 查询公告已读数量
	 */
	Long getAnnouncementReadCount(Long announcementId);

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
}
