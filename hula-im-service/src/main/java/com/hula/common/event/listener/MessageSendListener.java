package com.hula.common.event.listener;

import com.hula.common.constant.MqConstant;
import com.hula.common.domain.dto.MsgSendMessageDTO;
import com.hula.common.event.MessageSendEvent;
import com.hula.core.chat.dao.ContactDao;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.dao.RoomDao;
import com.hula.core.chat.dao.RoomFriendDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.entity.Room;
import com.hula.core.chat.domain.enums.HotFlagEnum;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.WeChatMsgOperationService;
import com.hula.core.chat.service.cache.GroupMemberCache;
import com.hula.core.chat.service.cache.HotRoomCache;
import com.hula.core.chat.service.cache.RoomCache;
import com.hula.core.user.service.cache.UserCache;
import com.hula.service.MQProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * 消息发送监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageSendListener {
    @Resource
    private ChatService chatService;
    @Resource
    private MessageDao messageDao;
//    @Resource
//    private IChatAIService openAIService;
    @Resource
    WeChatMsgOperationService weChatMsgOperationService;
    @Resource
    private RoomCache roomCache;
    @Resource
    private RoomDao roomDao;
    @Resource
    private GroupMemberCache groupMemberCache;
    @Resource
    private UserCache userCache;
    @Resource
    private RoomFriendDao roomFriendDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private ContactDao contactDao;
    @Resource
    private HotRoomCache hotRoomCache;
    @Resource
    private MQProducer mqProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getChatMsgSendDto().getMsgId();
        mqProducer.sendSecureMsg(MqConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId, event.getChatMsgSendDto().getUid()), msgId);
    }

    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void handlerMsg(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getChatMsgSendDto().getMsgId());
        Room room = roomCache.get(message.getRoomId());
        if (isHotRoom(room)) {
//            openAIService.chat(message);
        }
    }

    public boolean isHotRoom(Room room) {
        return Objects.equals(HotFlagEnum.YES.getType(), room.getHotFlag());
    }

    /**
     * 给用户微信推送艾特好友的消息通知
     * （这个没开启，微信不让推）
     */
    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void publishChatToWechat(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getChatMsgSendDto().getMsgId());
//        if (Objects.nonNull(message.getExtra().getAtUidList())) {
//            weChatMsgOperationService.publishChatMsgToWeChatUser(message.getFromUid(), message.getExtra().getAtUidList(),
//                    message.getContent());
//        }
    }
}
