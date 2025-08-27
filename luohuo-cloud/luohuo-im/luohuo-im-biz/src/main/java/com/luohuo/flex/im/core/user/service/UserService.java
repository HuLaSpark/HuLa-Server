package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.api.vo.UserRegisterVo;
import com.luohuo.flex.im.domain.dto.ItemInfoDTO;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.model.vo.query.BindEmailReq;
import com.luohuo.flex.im.domain.vo.req.user.BlackReq;
import com.luohuo.flex.im.domain.vo.req.user.ItemInfoReq;
import com.luohuo.flex.im.domain.vo.req.user.ModifyAvatarReq;
import com.luohuo.flex.im.domain.vo.req.user.ModifyNameReq;
import com.luohuo.flex.im.domain.vo.req.user.SummeryInfoReq;
import com.luohuo.flex.im.domain.vo.req.user.WearingBadgeReq;
import com.luohuo.flex.im.domain.vo.resp.user.BadgeResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;

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
	 * 校验邮箱是否存在
	 * @param email 邮箱
	 */
	Boolean checkEmail(String email);

	/**
	 * @param defUserId 主系统的userId
	 * @param tenantId
	 * @return
	 */
	Long getUIdByUserId(Long defUserId, Long tenantId);

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
    void modifyInfo(Long uid, ModifyNameReq req);


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

	Boolean register(UserRegisterVo userRegisterVo);
}
