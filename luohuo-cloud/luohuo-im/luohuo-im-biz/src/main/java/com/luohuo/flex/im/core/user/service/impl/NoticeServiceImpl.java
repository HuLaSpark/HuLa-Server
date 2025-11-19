package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luohuo.flex.im.core.user.dao.NoticeDao;
import com.luohuo.flex.im.core.user.service.NoticeService;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.Notice;
import com.luohuo.flex.im.domain.enums.NoticeStatusEnum;
import com.luohuo.flex.im.domain.enums.NoticeTypeEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import com.luohuo.flex.im.domain.vo.req.NoticeReq;
import com.luohuo.flex.im.domain.vo.res.NoticeVO;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.ws.WSNotice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum.UNREAD;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    
    private final NoticeDao noticeDao;
	private final PushService pushService;
	private final UserSummaryCache userSummaryCache;

	private void pushNoticeToUser(Long receiverId, Notice notice) {
		WsBaseResp<NoticeVO> wsMsg = new WsBaseResp<>();
		wsMsg.setData(convertToVO(notice));
		wsMsg.setType(WSRespTypeEnum.NOTIFY_EVENT.getType());
		pushService.sendPushMsg(wsMsg, Collections.singletonList(receiverId), notice.getSenderId());
	}

	@Override
	public Notice getByApplyId(Long uid, Long applyId) {
		return noticeDao.getBaseMapper().selectOne(new QueryWrapper<Notice>().eq("receiver_id", uid).eq("apply_id", applyId));
	}

	@Override
	public void createNotice(RoomTypeEnum applyType, NoticeTypeEnum type, Long senderId, Long receiverId, Long applyId, Long operate, String content) {
		createNotice(applyType, type, senderId, receiverId, applyId, operate, 0L, content);
	}

	@Override
	public void createNotice(RoomTypeEnum applyType, NoticeTypeEnum type, Long senderId, Long receiverId, Long applyId, Long operate, Long roomId, String content) {
		Notice notice = new Notice();
		notice.setType(applyType.getType());
		notice.setEventType(type.getType());
		notice.setSenderId(senderId);
		notice.setReceiverId(receiverId);
		notice.setApplyId(applyId);
		notice.setOperateId(operate);
		notice.setRoomId(roomId);
		notice.setContent(content);
		notice.setIsRead(UNREAD.getCode());
		notice.setStatus(NoticeStatusEnum.UNTREATED.getStatus());
		noticeDao.save(notice);

		// 实时推送
		pushNoticeToUser(receiverId, notice);
	}

	@Override
	public void updateNotice(Notice notice) {
		noticeDao.update(Wrappers.<Notice>lambdaUpdate().set(Notice::getStatus, notice.getStatus()).eq(Notice::getApplyId, notice.getApplyId()));

		// 实时推送
		pushNoticeToUser(notice.getReceiverId(), notice);
	}

	public void updateNotices(Notice notice) {
		noticeDao.update(Wrappers.<Notice>lambdaUpdate().set(Notice::getStatus, notice.getStatus()).eq(Notice::getApplyId, notice.getApplyId()));

		List<Notice> notices = noticeDao.getBaseMapper().selectList(new QueryWrapper<Notice>().eq("apply_id", notice.getApplyId()));
		for (Notice n : notices) {
			// 实时推送
			pushNoticeToUser(n.getReceiverId(), n);
		}
	}

	private void readNotices(Long uid, String applyType, IPage<Notice> noticeIPage) {
		List<Long> notices = noticeIPage.getRecords()
				.stream().map(Notice::getId)
				.collect(Collectors.toList());
		if(CollUtil.isNotEmpty(notices)){
			noticeDao.readNotices(uid, "friend".equals(applyType)? 2: 1, notices);
		}
	}

	@Override
	public PageBaseResp<NoticeVO> getUserNotices(Long uid, NoticeReq request) {
		IPage<Notice> noticeIPage = noticeDao.getUserNotices(uid, true, request.plusPage());
		// 将这些通知设为已读
		if(request.getClick()){
			readNotices(uid, request.getApplyType(), noticeIPage);
		}
		return PageBaseResp.init(noticeIPage, noticeIPage.getRecords().stream().map(notice -> convertToVO(notice)).collect(Collectors.toList()));
	}

	private NoticeVO convertToVO(Notice notice) {
		NoticeVO vo = new NoticeVO();
		vo.setId(notice.getId());
		vo.setApplyId(notice.getApplyId());
		vo.setSenderId(notice.getSenderId());
		vo.setReceiverId(notice.getReceiverId());
		vo.setOperateId(notice.getOperateId());
		vo.setRoomId(notice.getRoomId());
		vo.setContent(notice.getContent());
		vo.setEventType(notice.getEventType());
		vo.setType(notice.getType());
		vo.setStatus(notice.getStatus());
		vo.setCreateTime(notice.getCreateTime());
		vo.setRead(notice.getIsRead());

		// 填充发送人信息
		SummeryInfoDTO sender = userSummaryCache.get(notice.getSenderId());
		vo.setSenderName(sender.getName());
		vo.setSenderAvatar(sender.getAvatar());

		// 填充接收人信息
		SummeryInfoDTO receiver = userSummaryCache.get(notice.getReceiverId());
		vo.setReceiverName(receiver.getName());
		vo.setReceiverAvatar(receiver.getAvatar());
		return vo;
	}

	@Override
	public void markAsRead(Long noticeId) {
		noticeDao.markAsRead(noticeId);
	}

	@Override
	public WSNotice unread(Long uid) {
		return noticeDao.getUnReadCount(uid, uid);
	}
}