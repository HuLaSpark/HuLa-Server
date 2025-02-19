package com.hula.core.chat.service;


import com.hula.core.chat.domain.vo.request.admin.AdminAddReq;
import com.hula.core.chat.domain.vo.request.admin.AdminRevokeReq;
import com.hula.core.chat.domain.vo.request.member.MemberExitReq;

import java.util.List;

/**
 * <p>
 * 群成员表 服务类
 * </p>
 *
 * @author nyh
 */
public interface IGroupMemberService {
    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void addAdmin(Long uid, AdminAddReq request);

    /**
     * 撤销管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void revokeAdmin(Long uid, AdminRevokeReq request);

    /**
     * 退出群聊
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void exitGroup(Long uid, MemberExitReq request);
}
