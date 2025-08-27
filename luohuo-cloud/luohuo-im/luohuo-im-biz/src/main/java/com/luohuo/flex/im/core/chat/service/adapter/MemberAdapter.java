package com.luohuo.flex.im.core.chat.service.adapter;

import com.luohuo.flex.im.domain.entity.IpDetail;
import com.luohuo.flex.im.domain.entity.IpInfo;
import com.luohuo.flex.model.entity.ws.ChatMember;
import com.luohuo.flex.model.enums.ChatActiveStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.domain.entity.GroupMember;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;
import com.luohuo.flex.model.entity.ws.WSFeedMemberResp;
import com.luohuo.flex.model.entity.ws.WSMemberChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.luohuo.flex.model.entity.ws.WSMemberChange.CHANGE_TYPE_ADD;


/**
 * @author nyh
 */
@Component
@Slf4j
public class MemberAdapter {

	/**
	 * 将User对象转换为ChatMemberResp对象，并注入实时在线状态
	 * @param list 用户列表
	 * @param onlineUids 用户在线状态表
	 * @return 转换后的群成员响应对象列表
	 */
    public static List<ChatMemberResp> buildMember(List<User> list, Set<Long> onlineUids) {
		return list.stream().map(user -> {
			ChatMemberResp resp = new ChatMemberResp();
			resp.setUid(String.valueOf(user.getId()));
			resp.setName(user.getName());
			resp.setAvatar(user.getAvatar());
			resp.setAccount(user.getAccount());
			resp.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
			resp.setUserStateId(user.getUserStateId());
			Boolean isOnline = onlineUids.contains(user.getId());
			resp.setActiveStatus(isOnline? ChatActiveStatusEnum.ONLINE.getStatus(): ChatActiveStatusEnum.OFFLINE.getStatus());
			// 最后活跃时间（离线用户显示）
			if (!isOnline && user.getLastOptTime() != null) {
				resp.setLastOptTime(user.getLastOptTime());
			}
			return resp;
		}).collect(Collectors.toList());
    }

    public static List<ChatMemberListResp> buildMemberList(List<User> memberList) {
        return memberList.stream()
                .map(a -> {
                    ChatMemberListResp resp = new ChatMemberListResp();
                    BeanUtils.copyProperties(a, resp);
                    resp.setUid(a.getId());
                    return resp;
                }).collect(Collectors.toList());
    }

    public static List<ChatMemberListResp> buildMemberList(Map<Long, User> batch) {
        return buildMemberList(new ArrayList<>(batch.values()));
    }

    public static GroupMember buildMemberAdd(Long groupId, Long inviteeUid) {
		GroupMember member = new GroupMember();
		member.setGroupId(groupId);
		member.setUid(inviteeUid);
		member.setRoleId(GroupRoleEnum.MEMBER.getType());
		return member;
    }

	/**
	 * 组装数据，推送给群里在线的人员，推送内容为变动用户的数据
	 * @param roomId 房间号
	 * @param onlineUids 在线的人员
	 * @param map 用户的基础信息
	 * @return
	 */
	public static WsBaseResp<WSMemberChange> buildMemberAddWS(Long roomId, List<Long> onlineUids, List<ChatMember> memberResps, Map<Long, User> map) {
		WsBaseResp<WSMemberChange> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.memberChange.getType());
		WSMemberChange wsMemberChange = new WSMemberChange();

		memberResps.forEach(item -> {
			Long uid = Long.parseLong(item.getUid());
			User user = map.get(uid);

			if (user != null) {
				item.setActiveStatus(onlineUids.contains(user.getId()) ? ChatActiveStatusEnum.ONLINE.getStatus() : ChatActiveStatusEnum.OFFLINE.getStatus());
				item.setLastOptTime(user.getLastOptTime());
				item.setName(user.getName());
				item.setAvatar(user.getAvatar());
				item.setAccount(user.getAccount());
				item.setUserStateId(user.getUserStateId()+"");
			}
		});

		wsMemberChange.setChangeType(CHANGE_TYPE_ADD);
		wsMemberChange.setUserList(memberResps);
		wsMemberChange.setRoomId(roomId+"");
		wsBaseResp.setData(wsMemberChange);
		return wsBaseResp;
	}

    public static WsBaseResp<WSMemberChange> buildMemberRemoveWS(Long roomId, List<Long> uidList, Integer type) {
        WsBaseResp<WSMemberChange> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.memberChange.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();

		List<ChatMember> states = uidList.stream().map(uid -> {
			ChatMember chatMember = new ChatMember();
			chatMember.setUid(uid+"");
			return chatMember;
		}).collect(Collectors.toList());
		wsMemberChange.setUserList(states);
        wsMemberChange.setRoomId(roomId+"");
        wsMemberChange.setChangeType(type);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }

	/**
	 * 发朋友圈以后推送的消息
	 * @param uid 发布人
	 * @return
	 */
	public static WsBaseResp<WSFeedMemberResp> buildFeedPushWS(Long uid){
		WsBaseResp<WSFeedMemberResp> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.FEED_SEND_MSG.getType());
		wsBaseResp.setData(new WSFeedMemberResp(uid));
		return wsBaseResp;
	}
}
