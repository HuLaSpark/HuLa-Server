package com.hula.common.user.service.adapter;

import com.hula.common.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * @author nyh
 */
public class UserAdapter {
    public static User buildUserSave(String openId) {
        return User.builder().openId(openId).build();
    }

    public static User buildAuthorizeUser(Long uid, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(uid);
        user.setName(userInfo.getNickname());
        user.setAvatar(userInfo.getHeadImgUrl());
        return user;
    }
}
