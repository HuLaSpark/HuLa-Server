package com.hula.core.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import com.hula.core.chat.domain.vo.request.ChatMessageReq;
import com.hula.core.chat.service.adapter.MessageAdapter;
import com.hula.utils.AssertUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;


public abstract class AbstractMsgHandler<T> {
    @Resource
    private MessageDao messageDao;
    private Class<T> bodyClass;

    @PostConstruct
    private void init() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    /**
     * 消息类型
     */
    abstract MessageTypeEnum getMsgTypeEnum();

    protected void checkMsg(T body, Long roomId, Long uid) {

    }

    @Transactional
    public Long checkAndSaveMsg(ChatMessageReq request, Long uid) {
        T body = this.toBean(request.getBody());
        //统一校验
        AssertUtil.allCheckValidateThrow(body);
        //子类扩展校验
        checkMsg(body, request.getRoomId(), uid);
        Message insert = MessageAdapter.buildMsgSave(request, uid);
        //统一保存
        messageDao.save(insert);
        //子类扩展保存
        saveMsg(insert, body);
        return insert.getId();
    }

    private T toBean(Object body) {
        return bodyClass.isAssignableFrom(body.getClass()) ? (T) body : BeanUtil.toBean(body, bodyClass);
    }

    protected abstract void saveMsg(Message message, T body);

    /**
     * 展示消息
     */
    public abstract Object showMsg(Message msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);

}
