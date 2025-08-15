package com.luohuo.flex.im.core.chat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.flex.im.domain.vo.req.room.UserApplyResp;
import com.luohuo.flex.im.domain.vo.request.admin.AdminAddReq;
import com.luohuo.flex.im.domain.vo.request.admin.AdminRevokeReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactAddReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberExitReq;
import com.luohuo.flex.im.domain.vo.res.GroupListVO;
import jakarta.validation.Valid;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.entity.Announcements;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.vo.request.ChatMessageMemberReq;
import com.luohuo.flex.im.domain.vo.request.ContactFriendReq;
import com.luohuo.flex.im.domain.vo.request.GroupAddReq;
import com.luohuo.flex.im.domain.vo.request.RoomInfoReq;
import com.luohuo.flex.im.domain.vo.request.RoomMyInfoReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactHideReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactNotificationReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactShieldReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactTopReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberAddReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberDelReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberReq;
import com.luohuo.flex.im.domain.vo.request.room.AnnouncementsParam;
import com.luohuo.flex.im.domain.vo.request.room.ReadAnnouncementsParam;
import com.luohuo.flex.im.domain.vo.request.room.RoomGroupReq;
import com.luohuo.flex.im.domain.vo.response.AnnouncementsResp;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.model.entity.ws.ChatMessageResp;
import com.luohuo.flex.im.domain.vo.response.ChatRoomResp;
import com.luohuo.flex.im.domain.vo.response.MemberResp;
import com.luohuo.flex.im.domain.vo.req.MergeMessageReq;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;

import java.util.List;

/**
 * @author nyh
 */
public interface RoomAppService {
	/**
	 * 创建会话
	 * @return
	 */
	Boolean createContact(Long uid, @Valid ContactAddReq request);
    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);
	/**
	 * 获取用户所有会话列表
	 */
	List<ChatRoomResp> getContactPage(Long uid);

    /**
     * 获取群组信息
     */
    MemberResp getGroupDetail(Long uid, long roomId);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);

    void delMember(Long uid, MemberDelReq request);

    void addMember(Long uid, MemberAddReq request);

    Long addGroup(Long uid, GroupAddReq request);

    ChatRoomResp getContactDetail(Long uid, Long roomId);

    ChatRoomResp getContactDetailByFriend(Long uid, @Valid ContactFriendReq req);

    IPage<GroupListVO> groupList(Long uid,  IPage<GroupListVO>  page);

	void asyncOnline(List<Long> uidList, Long roomId, boolean online);


	/**
	 * 申请加群列表
	 * @param uid 登录用户id
	 * @param req 请求
	 */
	CursorPageBaseResp<UserApplyResp> queryApplyPage(Long uid, MemberReq req);

	/**
	 * 管理员修改群信息
	 * @return
	 */
	Boolean updateRoomInfo(Long uid, @Valid RoomInfoReq request);

	/**
	 * 群成员修改自己在群里的信息
	 * @return
	 */
	Boolean updateMyRoomInfo(Long uid, @Valid RoomMyInfoReq request);

	/**
	 * 置顶会话
	 * @return
	 */
	Boolean setTop(Long uid, ContactTopReq request);

	/**
	 * 发布公告
	 * @return
	 */
	Boolean pushAnnouncement(Long uid, AnnouncementsParam param);

	/**
	 * 编辑公告
	 * @return
	 */
	Boolean announcementEdit(Long uid, AnnouncementsParam param);

	/**
	 * 获取公告列表
	 * @return
	 */
	IPage<Announcements> announcementList(Long roomId, IPage<Announcements> page);

	/**
	 * 已读公告
	 */
	Boolean readAnnouncement(Long uid, ReadAnnouncementsParam param);

	/**
	 * 查看公告
	 */
	AnnouncementsResp getAnnouncement(Long uid, ReadAnnouncementsParam param);

	/**
	 * 删除公告
	 */
	Boolean announcementDelete(Long uid, Long id);

	/**
	 * 隐藏会话
	 * @return
	 */
	Boolean setHide(Long uid, ContactHideReq req);

	/**
	 * 将 id 的消息设置为免打扰
	 *
	 */
	Boolean setNotification(Long uid, ContactNotificationReq request);

	/**
	 * 查找群聊
	 */
	List<RoomGroup> searchGroup(@Valid RoomGroupReq req);

	/**
	 * 屏蔽/解除屏蔽
	 * @return
	 */
	Boolean setShield(Long uid, ContactShieldReq request);

	/**
	 * 合并消息
	 * @return
	 */
	ChatMessageResp mergeMessage(Long uid, MergeMessageReq req);

	/**
	 * 查询全部群成员
	 * @param request
	 */
	List<ChatMemberResp> listMember(@Valid MemberReq request);

	/**
	 * 解散群聊
	 */
	void exitGroup(Long uid, @Valid MemberExitReq request);

	/**
	 * 添加管理员
	 */
	void addAdmin(Long uid, @Valid AdminAddReq request);

	/**
	 * 移除管理员
	 */
	void revokeAdmin(Long uid, @Valid AdminRevokeReq request);
}
