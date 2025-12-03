package com.luohuo.flex.im.core.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.im.domain.entity.Contact;
import com.luohuo.flex.im.domain.entity.GroupMember;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReq;
import com.luohuo.flex.im.domain.vo.response.ChatMessageReadResp;
import com.luohuo.flex.model.entity.ws.ChatMyRoomGroupChange;
import com.luohuo.flex.model.entity.ws.ChatRoomGroupChange;
import com.luohuo.flex.im.domain.vo.response.ChatRoomResp;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息适配器
 * @author nyh
 */
public class RoomAdapter {


    public static List<ChatRoomResp> buildResp(List<Room> list) {
        return list.stream()
                .map(a -> {
                    ChatRoomResp resp = new ChatRoomResp();
                    BeanUtil.copyProperties(a, resp);
                    resp.setActiveTime(a.getActiveTime());
                    return resp;
                }).collect(Collectors.toList());
    }

    public static List<ChatMessageReadResp> buildReadResp(List<Contact> list) {
        return list.stream().map(contact -> {
            ChatMessageReadResp resp = new ChatMessageReadResp();
            resp.setUid(contact.getUid());
            return resp;
        }).collect(Collectors.toList());
    }

    public static List<GroupMember> buildGroupMemberBatch(List<Long> uidList, Long groupId) {
        return uidList.stream()
                .distinct()
                .map(uid -> {
                    GroupMember member = new GroupMember();
                    member.setRoleId(GroupRoleEnum.MEMBER.getType());
                    member.setUid(uid);
                    member.setGroupId(groupId);
                    return member;
                }).toList();
    }

	/**
	 * 创建群的消息
	 * @return
	 */
    public static ChatMessageReq buildGroupAddMessage(Boolean applyFor, Integer channel, Long roomId, User inviter, Map<Long, User> member) {
        ChatMessageReq chatMessageReq = new ChatMessageReq();
        chatMessageReq.setRoomId(roomId);
        chatMessageReq.setMsgType(MessageTypeEnum.SYSTEM.getType());
        String body = applyFor ? (channel != null && channel.equals(2) ? String.format("%s扫码进群", inviter.getName()) : String.format("%s加入群聊", inviter.getName()))
                : String.format("%s邀请%s加入群聊", inviter.getName(), member.values().stream().map(User::getName).collect(Collectors.joining("、")));
        chatMessageReq.setBody(body);
        return chatMessageReq;
    }

	/**
	 * 创建群聊基础信息变动ws
	 */
	public static WsBaseResp<ChatRoomGroupChange> buildRoomGroupChangeWS(Long roomId, String name, String avatar) {
		WsBaseResp<ChatRoomGroupChange> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.ROOM_INFO_CHANGE.getType());
		wsBaseResp.setData(new ChatRoomGroupChange(roomId.toString(), name, avatar));
		return wsBaseResp;
	}

	/**
	 * 创建群聊我的信息变动ws
	 */
	public static WsBaseResp<ChatMyRoomGroupChange> buildMyRoomGroupChangeWS(Long roomId, Long uid, String name) {
		WsBaseResp<ChatMyRoomGroupChange> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.MY_ROOM_INFO_CHANGE.getType());
		wsBaseResp.setData(new ChatMyRoomGroupChange(roomId.toString(), uid.toString(), name));
		return wsBaseResp;
	}

	/**
	 * 群聊被群主解散
	 */
	public static WsBaseResp<String> buildGroupDissolution(Long roomId) {
		WsBaseResp<String> WsBaseResp = new WsBaseResp<>();
		WsBaseResp.setType(WSRespTypeEnum.ROOM_DISSOLUTION.getType());
		WsBaseResp.setData(roomId+"");
		return WsBaseResp;
	}
}
