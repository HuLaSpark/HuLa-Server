package com.hula.core.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.utils.CursorUtils;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.enums.MessageStatusEnum;
import com.hula.core.chat.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public Integer getUnReadCount(Long roomId, Date readTime) {
        return Math.toIntExact(lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Objects.nonNull(readTime), Message::getCreateTime, readTime)
                .count());
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
}
