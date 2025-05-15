package com.hula.core.user.service;

import com.hula.core.user.domain.dto.ItemInfoDTO;
import com.hula.core.user.domain.dto.SummeryInfoDTO;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.req.user.*;
import com.hula.core.user.domain.vo.resp.user.BadgeResp;
import com.hula.core.user.domain.vo.resp.user.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author nyh
 */
public interface UserService {

	/**
	 * 获取用户基础信息 [DB]
	 *
	 * @param uid
	 *
	 */
	User getUserById(Long uid);

    /**
     * 获取前端展示信息
     *
     * @param uid
     *
     */
    UserInfoResp getUserInfo(Long uid);

    /**
     * 修改用户名
     *
     * @param uid
     * @param req
     */
    void modifyName(Long uid, ModifyNameReq req);


    /**
     * 修改头像
     * @param uid 用户id
     * @param req 请求体
     */
    void modifyAvatar(Long uid, ModifyAvatarReq req);

    /**
     * 用户徽章列表
     *
     * @param uid
     */
    List<BadgeResp> badges(Long uid);

    /**
     * 佩戴徽章
     *
     * @param uid
     * @param req
     */
    void wearingBadge(Long uid, WearingBadgeReq req);

    void black(BlackReq req);

    /**
     * 获取用户汇总信息
     *
     * @param req
     * @return
     */
    List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq req);

	/**
	 * 更改用户状态
	 * @param uid
	 * @param userStateId
	 * @return
	 */
	Boolean changeUserState(Long uid, Long userStateId);

	/**
	 * 绑定邮箱
	 * @param uid
	 * @param req
	 * @return
	 */
	Boolean bindEmail(Long uid, BindEmailReq req);

	/**
	 * 扣减ai调用次数
	 * @param uid
	 * @return
	 */
	Boolean subElectricity(Long uid);
}
