package com.luohuo.flex.im.core.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.im.common.enums.YesOrNoEnum;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.msg.ReplyMsg;
import com.luohuo.flex.im.domain.enums.MessageStatusEnum;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReq;
import com.luohuo.flex.im.core.chat.service.adapter.MessageAdapter;
import com.luohuo.flex.im.core.chat.service.cache.MsgCache;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.Optional;


public abstract class AbstractMsgHandler<T> {
    @Resource
    private MessageDao messageDao;

	@Resource
	private MsgCache msgCache;

	@Resource
	private UserSummaryCache userSummaryCache;

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
        // 统一校验
        AssertUtil.allCheckValidateThrow(body);
        // 子类扩展校验
        checkMsg(body, request.getRoomId(), uid);
        Message newMsg = MessageAdapter.buildMsgSave(request, uid);
        // 统一保存
        messageDao.save(newMsg);
        // 子类扩展保存
        saveMsg(newMsg, body);
        return newMsg.getId();
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
	 * 回复消息
	 * @param msg 消息主体
	 * @return
	 */
	public ReplyMsg replyMsg(Message msg){
		Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
				.map(msgCache::get)
				.filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));
		// TODO 这里的缓存不会立即删除，导致撤回消息后回复的信息还有 (nyh -> 2024-07-14 03:46:34)
		if (reply.isPresent()) {
			Message replyMessage = reply.get();
			ReplyMsg replyMsgVO = new ReplyMsg();
			replyMsgVO.setId(replyMessage.getId().toString());
			replyMsgVO.setUid(replyMessage.getFromUid().toString());
			replyMsgVO.setType(replyMessage.getType());
			replyMsgVO.setBody(MsgHandlerFactory.getStrategyNoNull(replyMessage.getType()).showReplyMsg(replyMessage));
			SummeryInfoDTO replyUser = userSummaryCache.get(replyMessage.getFromUid());
			replyMsgVO.setUsername(replyUser.getName());
			replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount()) && msg.getGapCount() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
			replyMsgVO.setGapCount(msg.getGapCount());
			return replyMsgVO;
		}
		return null;
	}

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);

}
