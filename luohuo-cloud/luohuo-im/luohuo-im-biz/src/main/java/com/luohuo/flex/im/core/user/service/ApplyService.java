package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.vo.req.friend.FriendApplyReq;
import com.luohuo.flex.im.domain.vo.request.RoomApplyReq;
import com.luohuo.flex.im.domain.vo.request.member.ApplyReq;
import com.luohuo.flex.im.domain.vo.request.member.GroupApplyHandleReq;
import jakarta.validation.Valid;

/**
 * <p>
 * 申请 服务类
 * </p>
 *
 * @author 乾乾
 */
public interface ApplyService {

    /**
     * 应用
     * 申请好友
     *
     * @param request 请求
     * @param uid     uid
     */
	UserApply handlerApply(Long uid, FriendApplyReq request);

	/**
	 * 申请加群
	 * @param request 请求
	 */
	Boolean applyGroup(Long uid, RoomApplyReq request);

	/**
	 * 审批申请
	 *
	 * @param uid     uid
	 * @param request 请求
	 */
	void handlerApply(Long uid, @Valid ApplyReq request);

    /**
     * 删除申请
     *
     * @param uid     uid
     * @param request 请求
     */
    void deleteApprove(Long uid, ApplyReq request);
}
