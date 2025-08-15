package com.luohuo.flex.im.core.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import com.luohuo.flex.im.common.enums.YesOrNoEnum;
import com.luohuo.flex.im.domain.entity.ItemConfig;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.UserBackpack;
import com.luohuo.flex.im.domain.vo.resp.user.BadgeResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author nyh
 */
public class UserAdapter {

    public static User buildUser(String openId) {
        User user = new User();
        user.setOpenId(openId);
        return user;
    }

    public static User buildAuthorizeUser(Long id, String account, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setAccount(account);
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }

    public static UserInfoResp buildUserInfoResp(User userInfo, Integer countByValidItemId) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setUid(userInfo.getId());
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        if (ObjectUtil.isNull(user)) {
            // 这里 user 入参可能为空，防止 NPE 问题
            return Collections.emptyList();
        }

        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(a -> {
                    BadgeResp resp = new BadgeResp();
                    BeanUtil.copyProperties(a, resp);
                    resp.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    resp.setWearing(ObjectUtil.equal(a.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    return resp;
                }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

}
