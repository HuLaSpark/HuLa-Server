package com.hula.core.user.service;

import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.req.PageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.domain.vo.res.PageBaseResp;
import com.hula.core.chat.domain.vo.request.friend.FriendPermissionReq;
import com.hula.core.chat.domain.vo.request.friend.FriendRemarkReq;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.user.domain.entity.UserApply;
import com.hula.core.user.domain.vo.req.friend.FriendApplyReq;
import com.hula.core.user.domain.vo.req.friend.FriendApproveReq;
import com.hula.core.user.domain.vo.req.friend.FriendCheckReq;
import com.hula.core.user.domain.vo.req.friend.FriendReq;
import com.hula.core.user.domain.vo.resp.friend.FriendApplyResp;
import com.hula.core.user.domain.vo.resp.friend.FriendCheckResp;
import com.hula.core.user.domain.vo.resp.friend.FriendResp;
import com.hula.core.user.domain.vo.resp.friend.FriendUnreadResp;
import jakarta.validation.Valid;

import java.util.List;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author nyh
 */
public interface FriendService {

	/**
	 * 创建双方好友关系
	 * @param uid 我自己
	 * @param targetUid 好友
	 */
	void createFriend(Long roomId, Long uid, Long targetUid);

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
	 * @param targetId 被申请人
	 * @param type 申请类型
	 * @return
	 */
	void createUserApply(Long uid, Long targetId, String msg, Integer type);

    /**
     * 应用
     * 申请好友
     *
     * @param request 请求
     * @param uid     uid
     */
	UserApply apply(Long uid, FriendApplyReq request);

    /**
     * 分页查询好友申请
     *
     * @param request 请求
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request);

    /**
     * 申请未读数
     *
     * @return {@link FriendUnreadResp}
     */
    FriendUnreadResp unread(Long uid);

	/**
	 * 与系统用户创建好友关系
	 * 与系统用户创建聊天框
	 * 系统用户发送欢迎消息
	 * 系统用户在群内发送欢迎消息
     */
	void createSystemFriend(List<Long> uidList);

    /**
     * 同意好友申请
     *
     * @param uid     uid
     * @param request 请求
     */
    void applyApprove(Long uid, FriendApproveReq request);

    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    void deleteFriend(Long uid, Long friendUid);

    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);

    /**
     * 拒绝
     *
     * @param uid     uid
     * @param request 请求
     */
    void reject(Long uid, FriendApproveReq request);

    /**
     * 忽略申请
     *
     * @param uid     uid
     * @param request 请求
     */
    void ignore(Long uid, FriendApproveReq request);

    /**
     * 删除申请
     *
     * @param uid     uid
     * @param request 请求
     */
    void deleteApprove(Long uid, FriendApproveReq request);

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
