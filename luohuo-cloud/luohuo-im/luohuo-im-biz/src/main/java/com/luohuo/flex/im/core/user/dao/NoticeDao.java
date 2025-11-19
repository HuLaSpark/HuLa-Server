package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.core.user.mapper.NoticeMapper;
import com.luohuo.flex.im.domain.entity.Notice;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendUnreadDto;
import com.luohuo.flex.model.entity.ws.WSNotice;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum.READ;
import static com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum.UNREAD;

/**
 * <p>
 * 通知 服务实现类
 * </p>
 *
 * @author 乾乾
 */
@Service
public class NoticeDao extends ServiceImpl<NoticeMapper, Notice> {

	public void markAsRead(Long noticeId) {
		Notice dbNotice = baseMapper.selectById(noticeId);

		Notice notice = new Notice();
		notice.setId(dbNotice.getId());
		notice.setIsRead(READ.getCode());
		baseMapper.updateById(notice);
	}

	/**
	 * 查询当前用户的通知
	 *
	 * @param uid        登录用户
	 * @param onlyUnread 通知状态
	 * @return
	 */
	public IPage<Notice> getUserNotices(Long uid, boolean onlyUnread, Page<Notice> page) {
		return lambdaQuery()
				.eq(Notice::getReceiverId, uid)
				.orderByDesc(Notice::getCreateTime)
				.page(page);
	}

	public WSNotice getUnReadCount(Long uid, Long receiverId) {
		List<FriendUnreadDto> unReadCountByTypeMap = baseMapper.getUnReadCountByType(receiverId, UNREAD.getCode());
		WSNotice wsNotice = new WSNotice();
		wsNotice.setUid(uid);

		for (FriendUnreadDto friendUnreadDto : unReadCountByTypeMap) {
			if (friendUnreadDto.getType().equals(RoomTypeEnum.FRIEND.getType())) {
				wsNotice.setUnReadCount4Friend(friendUnreadDto.getCount());
			} else {
				wsNotice.setUnReadCount4Group(friendUnreadDto.getCount());
			}
		}
		return wsNotice;
	}

	public void readNotices(Long uid, Integer type, List<Long> notices) {
		lambdaUpdate()
				.set(Notice::getIsRead, READ.getCode())
				.eq(Notice::getIsRead, UNREAD.getCode())
				.eq(Notice::getType, type)
				.in(Notice::getId, notices)
				.eq(Notice::getReceiverId, uid)
				.update();
	}
}
