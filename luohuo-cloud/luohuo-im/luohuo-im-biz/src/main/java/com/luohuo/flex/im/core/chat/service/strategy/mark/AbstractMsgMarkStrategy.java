package com.luohuo.flex.im.core.chat.service.strategy.mark;

import com.luohuo.basic.utils.SpringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.im.common.enums.YesOrNoEnum;
import com.luohuo.flex.im.common.event.MessageMarkEvent;
import com.luohuo.flex.im.core.chat.dao.MessageMarkDao;
import com.luohuo.flex.im.domain.dto.ChatMessageMarkDTO;
import com.luohuo.flex.im.domain.entity.MessageMark;
import com.luohuo.flex.im.domain.enums.MessageMarkActTypeEnum;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 消息标记抽象类
 */
public abstract class AbstractMsgMarkStrategy {
    @Resource
    private MessageMarkDao messageMarkDao;

    protected abstract MessageMarkTypeEnum getTypeEnum();

    @Transactional
    public void mark(Long uid, List<Long> uidList, Long msgId) {
        doMark(uid, uidList, msgId);
    }

    @Transactional
    public void unMark(Long uid, List<Long> uidList, Long msgId) {
        doUnMark(uid, uidList, msgId);
    }

    @PostConstruct
    private void init() {
        MsgMarkFactory.register(getTypeEnum().getType(), this);
    }

    protected void doMark(Long uid, List<Long> uidList, Long msgId) {
        exec(uid, uidList, msgId, MessageMarkActTypeEnum.MARK);
    }

    protected void doUnMark(Long uid, List<Long> uidList,Long msgId) {
        exec(uid, uidList, msgId, MessageMarkActTypeEnum.UN_MARK);
    }

    protected void exec(Long uid, List<Long> uidList, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        Integer markType = getTypeEnum().getType();
        Integer actType = actTypeEnum.getType();
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
        if (Objects.isNull(oldMark) && actTypeEnum == MessageMarkActTypeEnum.UN_MARK) {
            //取消的类型，数据库一定有记录，没有就直接跳过操作
            return;
        }
		//插入一条新消息,或者修改一条消息
        MessageMark insertOrUpdate = MessageMark.builder()
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actType))
                .build();
		insertOrUpdate.setId(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null));
        boolean modify = messageMarkDao.saveOrUpdate(insertOrUpdate);
        if (modify) {
            //修改成功才发布消息标记事件
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
			SpringUtils.publishEvent(new MessageMarkEvent(this, uidList, dto));
        }
    }

    private Integer transformAct(Integer actType) {
        if (actType == 1) {
            return YesOrNoEnum.NO.getStatus();
        } else if (actType == 2) {
            return YesOrNoEnum.YES.getStatus();
        }
        throw new BizException("动作类型 1确认 2取消");
    }

}
