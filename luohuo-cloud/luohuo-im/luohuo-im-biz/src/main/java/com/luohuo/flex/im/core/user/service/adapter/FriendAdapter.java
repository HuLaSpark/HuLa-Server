package com.luohuo.flex.im.core.user.service.adapter;

import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.entity.UserFriend;
import com.luohuo.flex.model.enums.ChatActiveStatusEnum;
import com.luohuo.flex.im.domain.vo.req.friend.FriendApplyReq;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendApplyResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendResp;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.luohuo.flex.im.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;
import static com.luohuo.flex.im.domain.enums.ApplyTypeEnum.ADD_FRIEND;


/**
 * 好友适配器
 * @author nyh
 */
public class FriendAdapter {

    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        UserApply userApplyNew = new UserApply();
        userApplyNew.setUid(uid);
		userApplyNew.setRoomId(0L);
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

	/**
	 * @param friendPage 好友列表
	 * @param userOnlineMap 用户在线状态映射
	 * @return
	 */
    public static List<FriendResp> buildFriend(List<UserFriend> friendPage, Map<Long, Boolean> userOnlineMap) {
		Map<Long, UserFriend> friendHashMap = friendPage.stream().collect(Collectors.toMap(friend -> friend.getFriendUid(), Function.identity()));
        return friendPage.stream().map(userFriend -> {
            FriendResp resp = new FriendResp();
            resp.setUid(userFriend.getFriendUid());
			UserFriend friend = friendHashMap.get(userFriend.getFriendUid());
			resp.setHideMyPosts(friend.getHideMyPosts());
			resp.setHideTheirPosts(friend.getHideTheirPosts());
			resp.setActiveStatus(userOnlineMap.get(userFriend.getFriendUid())? ChatActiveStatusEnum.ONLINE.getStatus(): ChatActiveStatusEnum.OFFLINE.getStatus());
            return resp;
        }).collect(Collectors.toList());
    }
}
