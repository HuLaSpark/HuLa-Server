package com.hula.core.chat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.core.chat.domain.entity.Announcements;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.vo.request.ChatMessageMemberReq;
import com.hula.core.chat.domain.vo.request.ContactFriendReq;
import com.hula.core.chat.domain.vo.request.GroupAddReq;
import com.hula.core.chat.domain.vo.request.RoomApplyReq;
import com.hula.core.chat.domain.vo.request.RoomInfoReq;
import com.hula.core.chat.domain.vo.request.RoomMyInfoReq;
import com.hula.core.chat.domain.vo.request.contact.ContactHideReq;
import com.hula.core.chat.domain.vo.request.contact.ContactNotificationReq;
import com.hula.core.chat.domain.vo.request.contact.ContactShieldReq;
import com.hula.core.chat.domain.vo.request.contact.ContactTopReq;
import com.hula.core.chat.domain.vo.request.member.MemberAddReq;
import com.hula.core.chat.domain.vo.request.member.MemberDelReq;
import com.hula.core.chat.domain.vo.request.member.MemberReq;
import com.hula.core.chat.domain.vo.request.room.AnnouncementsParam;
import com.hula.core.chat.domain.vo.request.room.ReadAnnouncementsParam;
import com.hula.core.chat.domain.vo.request.room.RoomGroupReq;
import com.hula.core.chat.domain.vo.response.AnnouncementsResp;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.chat.domain.vo.response.ChatRoomResp;
import com.hula.core.chat.domain.vo.response.MemberResp;
import com.hula.core.user.domain.vo.resp.ws.ChatMemberResp;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author nyh
 */
public interface RoomAppService {
    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);

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

	/**
	 * 申请加群
	 * @param request 请求
	 */
	Boolean applyGroup(Long uid, RoomApplyReq request);

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
}
