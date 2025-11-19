package com.luohuo.flex.im.core.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.im.common.enums.YesOrNoEnum;
import com.luohuo.flex.im.domain.entity.Announcements;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.MessageMark;
import com.luohuo.flex.im.domain.vo.response.msg.AudioCallMsgDTO;
import com.luohuo.flex.im.domain.vo.response.msg.BodyDTO;
import com.luohuo.flex.im.domain.vo.response.msg.VideoCallMsgDTO;
import com.luohuo.flex.im.domain.vo.response.msg.MergeMsgDTO;
import com.luohuo.flex.im.domain.vo.response.msg.NoticeMsgDTO;
import com.luohuo.flex.model.entity.ws.AdminChangeDTO;
import com.luohuo.flex.model.entity.ws.WSNotice;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;
import com.luohuo.flex.im.domain.enums.MessageStatusEnum;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReq;
import com.luohuo.flex.im.domain.entity.msg.TextMsgReq;
import com.luohuo.flex.model.entity.ws.ChatMessageResp;
import com.luohuo.flex.im.domain.vo.response.ReadAnnouncementsResp;
import com.luohuo.flex.im.core.chat.service.strategy.msg.AbstractMsgHandler;
import com.luohuo.flex.im.core.chat.service.strategy.msg.MsgHandlerFactory;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.ws.CallEndReq;

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
		if(marks == null || marks.isEmpty()) return new HashMap<>();
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

	public static ChatMessageReq buildAgreeMsg(Long roomId, Boolean isPush) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setMsgType(MessageTypeEnum.TEXT.getType());
		chatMessageReq.setPushMessage(isPush);
		TextMsgReq textMsgReq = new TextMsgReq();
		textMsgReq.setContent("我们已经成为好友了，开始聊天吧");
		chatMessageReq.setBody(textMsgReq);
		return chatMessageReq;
	}

	/**
	 * 构造音视频消息
	 * @param callEndReq 构造参数
	 * @return
	 */
	public static ChatMessageReq buildMediumMsg(CallEndReq callEndReq) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(callEndReq.getRoomId());
		chatMessageReq.setMsgType(callEndReq.getMediumType()? MessageTypeEnum.VIDEO_CALL.getType(): MessageTypeEnum.AUDIO_CALL.getType());
		chatMessageReq.setPushMessage(false);
		long duration = 0;
		if(Objects.nonNull(callEndReq.getStartTime()) && Objects.nonNull(callEndReq.getEndTime())){
			long time = callEndReq.getEndTime() - callEndReq.getStartTime();
			duration = (long) Math.ceil(time / 1000.0);
		}

		// 群聊消息
		if(callEndReq.getMediumType()){
			VideoCallMsgDTO videoCallMsgDTO = new VideoCallMsgDTO();
			videoCallMsgDTO.setBegin(callEndReq.getBegin());
			videoCallMsgDTO.setCreator(callEndReq.getUid());
			videoCallMsgDTO.setDuration(duration);
			videoCallMsgDTO.setIsGroup(callEndReq.getIsGroup());
			videoCallMsgDTO.setState(callEndReq.getState());
			videoCallMsgDTO.setStartTime(callEndReq.getStartTime());
			videoCallMsgDTO.setEndTime(callEndReq.getEndTime());
			chatMessageReq.setBody(videoCallMsgDTO);
		} else {
			AudioCallMsgDTO videoCallMsgDTO = new AudioCallMsgDTO();
			videoCallMsgDTO.setDuration(duration);
			videoCallMsgDTO.setIsGroup(callEndReq.getIsGroup());
			videoCallMsgDTO.setCreator(callEndReq.getUid());
			videoCallMsgDTO.setState(callEndReq.getState());
			videoCallMsgDTO.setStartTime(callEndReq.getStartTime());
			videoCallMsgDTO.setEndTime(callEndReq.getEndTime());
			chatMessageReq.setBody(videoCallMsgDTO);
		}

		return chatMessageReq;
	}

	public static ChatMessageReq buildAgreeMsg4Group(Long roomId, Long count, String userName) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setMsgType(MessageTypeEnum.BOT.getType());
		chatMessageReq.setSkip(true);
		TextMsgReq textMsgReq = new TextMsgReq();
		textMsgReq.setContent(String.format("欢迎[%s]第%d位加入HuLa", userName, count));
		chatMessageReq.setBody(textMsgReq);
		return chatMessageReq;
	}

	/**
	 * 合并消息
	 */
	public static ChatMessageReq buildMergeMsg(Long roomId, List<Message> messages) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setSkip(true);
		chatMessageReq.setMsgType(MessageTypeEnum.MERGE.getType());
		chatMessageReq.setBody(new MergeMsgDTO(messages.stream().map(msg -> new BodyDTO(msg.getFromUid().toString(), msg.getId().toString())).toList()));
		return chatMessageReq;
	}

	/**
	 * 群公告消息
	 */
	public static ChatMessageReq buildAnnouncementsMsg(Long roomId, Announcements announcements) {
		ChatMessageReq chatMessageReq = new ChatMessageReq();
		chatMessageReq.setRoomId(roomId);
		chatMessageReq.setSkip(true);
		chatMessageReq.setMsgType(MessageTypeEnum.NOTICE.getType());
		NoticeMsgDTO noticeMsgDTO = new NoticeMsgDTO();
		noticeMsgDTO.setId(announcements.getId());
		noticeMsgDTO.setUid(announcements.getUid());
		noticeMsgDTO.setTop(announcements.getTop());
		noticeMsgDTO.setRoomId(announcements.getRoomId());
		noticeMsgDTO.setContent(announcements.getContent());
		noticeMsgDTO.setCreateTime(TimeUtils.getTime(announcements.getUpdateTime()));
		chatMessageReq.setBody(noticeMsgDTO);
		return chatMessageReq;
	}

	/**
	 * 邀请用户进群通知
	 * @param resp 通知数据
	 */
	public static WsBaseResp<WSNotice> buildInviteeUserAddGroupMessage(WSNotice resp) {
		WsBaseResp<WSNotice> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.NEW_APPLY.getType());
		wsBaseResp.setData(resp);
		return wsBaseResp;
	}

	/**
	 * 构建设置管理员
	 */
	public static WsBaseResp<AdminChangeDTO> buildSetAdminMessage(AdminChangeDTO adminChangeDTO) {
		WsBaseResp<AdminChangeDTO> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.GROUP_SET_ADMIN.getType());
		wsBaseResp.setData(adminChangeDTO);
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
