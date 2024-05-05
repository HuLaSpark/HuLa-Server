package com.hula.core.user.service;

import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.req.BlackReq;
import com.hula.core.user.domain.vo.resp.BadgeResp;
import com.hula.core.user.domain.vo.resp.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author nyh
 * @since 2024-04-06
 */
public interface UserService {

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, Long itemId);

    void black(BlackReq req);
}
