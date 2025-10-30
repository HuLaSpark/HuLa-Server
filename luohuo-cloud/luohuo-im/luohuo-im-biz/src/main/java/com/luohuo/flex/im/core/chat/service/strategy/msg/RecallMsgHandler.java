package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.im.core.chat.service.cache.GroupMemberCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomCache;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.GroupMember;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import com.luohuo.flex.im.vo.result.tenant.MsgRecallVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.common.event.MessageRecallEvent;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.model.entity.dto.ChatMsgRecallDTO;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.vo.response.msg.MsgRecallDTO;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 撤回文本消息
 * @author nyh
 */
@Component
@AllArgsConstructor
public class RecallMsgHandler extends AbstractMsgHandler<Object> {

	private RoomCache roomCache;
	private MessageDao messageDao;
	private UserSummaryCache userSummaryCache;
	private GroupMemberCache groupMemberCache;

	@Override
	MessageTypeEnum getMsgTypeEnum() {
		return MessageTypeEnum.RECALL;
	}

	@Override
	public void saveMsg(Message msg, Object body) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object showMsg(Message msg) {
		MsgRecallDTO recall = msg.getExtra().getRecall();
		Long recallerUid = recall.getRecallUid();
		Long senderUid = msg.getFromUid();
		Long currentUserUid = ContextUtil.getUid();

		String roleName = "";
		Room room = roomCache.get(msg.getRoomId());
		if(room.getType().equals(RoomTypeEnum.GROUP.getType())){
			GroupMember recallerMember = groupMemberCache.getMemberDetail(msg.getRoomId(), recallerUid);
			roleName = GroupRoleEnum.get(recallerMember.getRoleId());
		}

		// 获取撤回者的群成员信息和用户信息
		SummeryInfoDTO recallerUserInfo = userSummaryCache.get(recallerUid);

		// 获取被撤回消息发送者的用户信息（用于显示成员名称）
		SummeryInfoDTO senderUserInfo = userSummaryCache.get(senderUid);

		// 判断关键关系
		boolean isRecallerCurrentUser = Objects.equals(recallerUid, currentUserUid);
		boolean isSenderCurrentUser = Objects.equals(senderUid, currentUserUid);


		String messageText;

		if (isRecallerCurrentUser) {
			// 当前用户是撤回操作执行者
			if (Objects.equals(recallerUid, senderUid)) {
				// 撤回自己的消息：自己视角
				messageText = "你撤回了一条消息";
			} else {
				// 撤回他人的消息：群主/管理员视角
				messageText = "你撤回了成员" + senderUserInfo.getName() + "的一条消息";
			}
		} else {
			// 当前用户不是撤回操作执行者
			if (isSenderCurrentUser) {
				// 当前用户是被撤回消息的发送者（被撤回者视角）
				messageText = roleName + recallerUserInfo.getName() + "撤回了你的一条消息";
			} else {
				// 当前用户是旁观者（其他成员视角）
				messageText = roleName + recallerUserInfo.getName() + "撤回了一条消息";
			}
		}

		return new MsgRecallVo(messageText);
	}

	@Override
	public Object showReplyMsg(Message msg) {
		return "原消息已被撤回";
	}

	public void recall(Long recallUid, List<Long> uidList, Message message) {//todo 消息覆盖问题用版本号解决
		MessageExtra extra = message.getExtra();
		extra.setRecall(new MsgRecallDTO(recallUid, TimeUtils.getTime(LocalDateTime.now())));
		Message update = new Message();
		update.setId(message.getId());
		update.setType(MessageTypeEnum.RECALL.getType());
		update.setExtra(extra);
		messageDao.updateById(update);
		SpringUtils.publishEvent(new MessageRecallEvent(this, uidList, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));
	}

	@Override
	public String showContactMsg(Message msg) {
		return "撤回了一条消息";
//		return ((MsgRecallVo) showMsg(msg)).getContent();
	}
}
