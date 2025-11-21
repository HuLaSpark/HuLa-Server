package com.luohuo.flex.im.core.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.Contact;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.enums.MessageStatusEnum;
import com.luohuo.flex.im.core.chat.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    public CursorPageBaseResp<Message> getCursorPage(Long roomId, CursorPageBaseReq request, Long lastMsgId) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
            wrapper.le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId);
        }, Message::getId);
    }

    /**
     * 乐观更新消息类型
     */
    public boolean riseOptimistic(Long id, Integer oldType, Integer newType) {
        return lambdaUpdate()
                .eq(Message::getId, id)
                .eq(Message::getType, oldType)
                .set(Message::getType, newType)
                .update();
    }

    public Integer getGapCount(Long roomId, Long fromId, Long toId) {
        return Math.toIntExact(lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, fromId)
                .le(Message::getId, toId)
                .count());
    }

    public void invalidByUid(Long uid) {
        lambdaUpdate()
                .eq(Message::getFromUid, uid)
                .set(Message::getStatus, MessageStatusEnum.DELETE.getStatus())
                .update();
    }

    /**
     * 根据房间ID逻辑删除消息
     *
     * @param roomId  房间ID
     * @param uidList 群成员列表
     * @return 是否删除成功
     */
    public Boolean removeByRoomId(Long roomId, List<Long> uidList) {
		LambdaUpdateWrapper<Message> wrapper = new UpdateWrapper<Message>().lambda()
				.eq(Message::getRoomId, roomId)
				.set(Message::getStatus, MessageStatusEnum.DELETE.getStatus());

        if (CollectionUtil.isNotEmpty(uidList)) {
			wrapper.in(Message::getFromUid, uidList);
        }
		return this.update(wrapper);
    }

	public Map<Long, Integer> batchGetUnReadCount(Long uid, Collection<Contact> contactList) {
		List<Map<String, Object>> rows = baseMapper.batchGetUnReadCount(uid, contactList);
		if (CollectionUtil.isEmpty(rows)) {
			return new HashMap<>();
		}
		return rows.stream().collect(Collectors.toMap(
				m -> ((Number) m.get("room_id")).longValue(),
				m -> {
					Object v = m.get("unread_count");
					return v == null ? 0 : ((Number) v).intValue();
				}
		));
	}
}
