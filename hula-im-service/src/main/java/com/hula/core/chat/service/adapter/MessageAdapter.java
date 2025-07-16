package com.hula.core.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.hula.common.enums.YesOrNoEnum;
import com.hula.core.chat.domain.entity.Announcements;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.entity.MessageMark;
import com.hula.core.chat.domain.entity.msg.MergeMsg;
import com.hula.core.chat.domain.entity.msg.MergeMsgDTO;
import com.hula.core.chat.domain.entity.msg.NoticeMsgDTO;
import com.hula.core.chat.domain.enums.MessageMarkTypeEnum;
import com.hula.core.chat.domain.enums.MessageStatusEnum;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import com.hula.core.chat.domain.vo.request.ChatMessageReq;
import com.hula.core.chat.domain.vo.request.msg.TextMsgReq;
import com.hula.core.chat.domain.vo.response.ChatMessageResp;
import com.hula.core.chat.domain.vo.response.ReadAnnouncementsResp;
import com.hula.core.chat.service.strategy.msg.AbstractMsgHandler;
import com.hula.core.chat.service.strategy.msg.MsgHandlerFactory;
import com.hula.core.user.domain.enums.UserTypeEnum;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.enums.WsBaseResp;

import java.util.*;
import java.util.stream.Collectors;


public class MessageAdapter {
    public static final int CAN_CALLBACK_GAP_COUNT = 100;

    public static Message buildMsgSave(ChatMessageReq request, Long uid) {

        return Message.builder()
                .fromUid(uid)
                .roomId(request.getRoomId())
                .type(request.getMsgType())
                .status(MessageStatusEnum.NORMAL.getStatus())
				.createTime(DateUtil.date())
				.updateTime(DateUtil.date())
                .build();

    }

    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, List<MessageMark> msgMark, Long receiveUid) {
        Map<Long, List<MessageMark>> markMap = msgMark.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messages.stream().map(a -> {
            ChatMessageResp resp = new ChatMessageResp();
            resp.setFromUser(buildFromUser(a.getFromUid()));
            resp.setMessage(buildMessage(a, markMap.getOrDefault(a.getId(), new ArrayList<>()), receiveUid));
            return resp;
        })
                .sorted(Comparator.comparing(a -> a.getMessage().getSendTime()))
                //帮前端排好序，更方便它展示
                .collect(Collectors.toList());
    }

    private static ChatMessageResp.Message buildMessage(Message message, List<MessageMark> marks, Long receiveUid) {
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        if (Objects.nonNull(msgHandler)) {
            messageVO.setBody(msgHandler.showMsg(message));
        }
        //消息标记
        messageVO.setMessageMarks(buildMsgMark(marks, receiveUid));
        return messageVO;
    }

	private static Map<Integer, ChatMessageResp.MarkItem> buildMsgMark(List<MessageMark> marks, Long receiveUid) {
		Map<Integer, List<MessageMark>> typeMap = marks.stream().collect(Collectors.groupingBy(MessageMark::getType));

		// 构造动态统计为主数据源
		Map<Integer, ChatMessageResp.MarkItem> stats = new HashMap<>();

		// 批量映射操作数量
		Arrays.stream(MessageMarkTypeEnum.values()).forEach(typeEnum -> {
			List<MessageMark> list = typeMap.getOrDefault(typeEnum.getType(), Collections.emptyList());

			stats.put(typeEnum.getType(), new ChatMessageResp.MarkItem(list.size(), Optional.ofNullable(receiveUid)
					.filter(uid -> list.stream().anyMatch(m -> m != null && Objects.equals(m.getUid(), uid)))
					.map(uid -> YesOrNoEnum.YES.getBool())
					.orElse(YesOrNoEnum.NO.getBool())));
		});

		return stats;
	}

    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid.toString());
        return userInfo;
    }

	public static ChatMessageReq buildAgreeMsg(Long roomId, UserTypeEnum userTypeEnum, Boolean isPush) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setMsgType(MessageTypeEnum.TEXT.getType());
		chatMessageReq.setPushMessage(isPush);
		TextMsgReq textMsgReq = new TextMsgReq();
		if (userTypeEnum == UserTypeEnum.NORMAL){
			textMsgReq.setContent("我们已经成为好友了，开始聊天吧");
		}else if (userTypeEnum == UserTypeEnum.SYSTEM){
			textMsgReq.setContent("欢迎来到Hula");
		}
		chatMessageReq.setBody(textMsgReq);
		return chatMessageReq;
	}

	public static ChatMessageReq buildAgreeMsg4Group(Long roomId, String userName) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setMsgType(MessageTypeEnum.BOT.getType());
		chatMessageReq.setSkip(true);
		TextMsgReq textMsgReq = new TextMsgReq();
		textMsgReq.setContent(String.format("欢迎[%s]加入群聊", userName));
		chatMessageReq.setBody(textMsgReq);
		return chatMessageReq;
	}

	/**
	 * 合并消息
	 */
	public static ChatMessageReq buildMergeMsg(Long roomId, List<MergeMsg> messages) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setMsgType(MessageTypeEnum.MERGE.getType());
		MergeMsgDTO mergeMsgDTO = new MergeMsgDTO();
		mergeMsgDTO.setMessages(messages);
		chatMessageReq.setBody(mergeMsgDTO);
		return chatMessageReq;
	}

	/**
	 * 群公告消息
	 */
	public static ChatMessageReq buildAnnouncementsMsg(Long roomId, Announcements announcements) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setMsgType(MessageTypeEnum.NOTICE.getType());
		NoticeMsgDTO noticeMsgDTO = new NoticeMsgDTO();
		noticeMsgDTO.setId(announcements.getId());
		noticeMsgDTO.setUid(announcements.getUid());
		noticeMsgDTO.setTop(announcements.getTop());
		noticeMsgDTO.setRoomId(announcements.getRoomId());
		noticeMsgDTO.setContent(announcements.getContent());
		noticeMsgDTO.setCreatedTime(announcements.getCreatedTime());
		chatMessageReq.setBody(noticeMsgDTO);
		return chatMessageReq;
	}

	/**
	 * 群通知
	 */
	public static WsBaseResp<String> buildRoomGroupMessage(String msg) {
		WsBaseResp<String> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.ROOM_GROUP_MSG.getType());
		wsBaseResp.setData(msg);
		return wsBaseResp;
	}

	/**
	 * 已读群公告
	 */
	public static WsBaseResp<ReadAnnouncementsResp> buildReadRoomGroupAnnouncement(ReadAnnouncementsResp resp) {
		WsBaseResp<ReadAnnouncementsResp> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.ROOM_GROUP_NOTICE_READ_MSG.getType());
		wsBaseResp.setData(resp);
		return wsBaseResp;
	}

}
