package com.hula.core.user.service.adapter;


import cn.hutool.core.util.StrUtil;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.entity.UserApply;
import com.hula.core.user.domain.entity.UserFriend;
import com.hula.core.user.domain.vo.req.friend.FriendApplyReq;
import com.hula.core.user.domain.vo.resp.friend.FriendApplyResp;
import com.hula.core.user.domain.vo.resp.friend.FriendResp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hula.core.user.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.hula.core.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;
import static com.hula.core.user.domain.enums.ApplyTypeEnum.ADD_FRIEND;


/**
 * 好友适配器
 * @author nyh
 */
public class FriendAdapter {


    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        UserApply userApplyNew = new UserApply();
        userApplyNew.setUid(uid);
        userApplyNew.setMsg(request.getMsg());
        userApplyNew.setType(ADD_FRIEND.getCode());
        userApplyNew.setTargetId(request.getTargetUid());
        userApplyNew.setStatus(WAIT_APPROVAL.getCode());
        userApplyNew.setReadStatus(UNREAD.getCode());
        return userApplyNew;
    }

    public static List<FriendApplyResp> buildFriendApplyList(List<UserApply> records) {
        return records.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setUid(userApply.getUid());
			friendApplyResp.setTargetId(userApply.getTargetId());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setStatus(userApply.getStatus());
			friendApplyResp.setCreateTime(userApply.getCreateTime());
            return friendApplyResp;
        }).collect(Collectors.toList());
    }

    public static List<FriendResp> buildFriend(List<UserFriend> friendPage, List<User> userList) {
		Map<Long, UserFriend> friendHashMap = friendPage.stream().collect(Collectors.toMap(friend -> friend.getFriendUid(), Function.identity()));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return friendPage.stream().map(userFriend -> {
            FriendResp resp = new FriendResp();
            resp.setUid(userFriend.getFriendUid());
            User user = userMap.get(userFriend.getFriendUid());
            if (Objects.nonNull(user)) {
				UserFriend friend = friendHashMap.get(userFriend.getFriendUid());
				resp.setHideMyPosts(friend.getHideMyPosts());
				resp.setHideTheirPosts(friend.getHideTheirPosts());
                resp.setActiveStatus(user.getActiveStatus());
            }
            return resp;
        }).collect(Collectors.toList());
    }
}
