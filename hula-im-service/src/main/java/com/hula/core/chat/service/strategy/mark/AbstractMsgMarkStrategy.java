package com.hula.core.chat.service.strategy.mark;

import com.hula.common.enums.YesOrNoEnum;
import com.hula.common.event.MessageMarkEvent;
import com.hula.core.chat.dao.MessageMarkDao;
import com.hula.core.chat.domain.dto.ChatMessageMarkDTO;
import com.hula.core.chat.domain.entity.MessageMark;
import com.hula.core.chat.domain.enums.MessageMarkActTypeEnum;
import com.hula.core.chat.domain.enums.MessageMarkTypeEnum;
import com.hula.exception.BizException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * 消息标记抽象类
 */
public abstract class AbstractMsgMarkStrategy {
    @Resource
    private MessageMarkDao messageMarkDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    protected abstract MessageMarkTypeEnum getTypeEnum();

    @Transactional
    public void mark(Long uid, Long msgId) {
        doMark(uid, msgId);
    }

    @Transactional
    public void unMark(Long uid, Long msgId) {
        doUnMark(uid, msgId);
    }

    @PostConstruct
    private void init() {
        MsgMarkFactory.register(getTypeEnum().getType(), this);
    }

    protected void doMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.MARK);
    }

    protected void doUnMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.UN_MARK);
    }

    protected void exec(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        Integer markType = getTypeEnum().getType();
        Integer actType = actTypeEnum.getType();
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
        if (Objects.isNull(oldMark) && actTypeEnum == MessageMarkActTypeEnum.UN_MARK) {
            //取消的类型，数据库一定有记录，没有就直接跳过操作
            return;
        }
        //插入一条新消息,或者修改一条消息
        MessageMark insertOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actType))
                .build();
        boolean modify = messageMarkDao.saveOrUpdate(insertOrUpdate);
        if (modify) {
            //修改成功才发布消息标记事件
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
            applicationEventPublisher.publishEvent(new MessageMarkEvent(this, dto));
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
