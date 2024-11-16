package com.hula.core.chat.service.strategy.msg;

import cn.hutool.core.collection.CollectionUtil;
import com.hula.common.enums.YesOrNoEnum;
import com.hula.utils.AssertUtil;
import com.hula.common.utils.discover.PrioritizedUrlDiscover;
import com.hula.common.utils.discover.domain.UrlInfo;
import com.hula.common.utils.sensitiveword.SensitiveWordBs;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.entity.msg.MessageExtra;
import com.hula.core.chat.domain.enums.MessageStatusEnum;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import com.hula.core.chat.domain.vo.request.msg.TextMsgReq;
import com.hula.core.chat.domain.vo.response.msg.TextMsgResp;
import com.hula.core.chat.service.adapter.MessageAdapter;
import com.hula.core.chat.service.cache.MsgCache;
import com.hula.core.chat.service.cache.MsgPlusCache;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.RoleEnum;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.user.service.cache.UserInfoCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 普通文本消息
 * @author nyh
 */
@Component
@AllArgsConstructor
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {

    private MessageDao messageDao;
    private MsgCache msgCache;
    private UserCache userCache;
    private UserInfoCache userInfoCache;
    private RoleService roleService;
    private SensitiveWordBs sensitiveWordBs;
    private MsgPlusCache msgPlusCache;

    private static final PrioritizedUrlDiscover URL_TITLE_DISCOVER = new PrioritizedUrlDiscover();

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    @Override
    protected void checkMsg(TextMsgReq body, Long roomId, Long uid) {
        //校验下回复消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Message replyMsg = messageDao.getById(body.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), roomId, "只能回复相同会话内的消息");
        }
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {
            //前端传入的@用户列表可能会重复，需要去重
            List<Long> atUidList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            Map<Long, User> batch = userInfoCache.getBatch(atUidList);
            //如果@用户不存在，userInfoCache 返回的map中依然存在该key，但是value为null，需要过滤掉再校验
            long batchCount = batch.values().stream().filter(Objects::nonNull).count();
            AssertUtil.equal((long)atUidList.size(), batchCount, "@用户不存在");
            boolean atAll = body.getAtUidList().contains(0L);
            if (atAll) {
                AssertUtil.isTrue(roleService.hasPower(uid, RoleEnum.CHAT_MANAGER), "没有权限");
            }
        }
    }

    @Override
    public void saveMsg(Message msg, TextMsgReq body) {//插入文本内容
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setContent(sensitiveWordBs.filter(body.getContent()));
        update.setExtra(extra);
        //如果有回复消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Integer gapCount = messageDao.getGapCount(msg.getRoomId(), body.getReplyMsgId(), msg.getId());
            update.setGapCount(gapCount);
            update.setReplyMsgId(body.getReplyMsgId());

        }
        //判断消息url跳转
        Map<String, UrlInfo> urlContentMap = URL_TITLE_DISCOVER.getUrlContentMap(body.getContent());
        extra.setUrlContentMap(urlContentMap);
        //艾特功能
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {
            extra.setAtUidList(body.getAtUidList());

        }

        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        resp.setUrlContentMap(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getUrlContentMap).orElse(null));
        resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
        //回复消息
        Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
                .map(msgPlusCache::get)
                .filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));
        // TODO 这里的缓存不会立即删除，导致撤回消息后回复的信息还有 (nyh -> 2024-07-14 03:46:34)
        if (reply.isPresent()) {
            Message replyMessage = reply.get();
            TextMsgResp.ReplyMsg replyMsgVO = new TextMsgResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setUid(replyMessage.getFromUid());
            replyMsgVO.setType(replyMessage.getType());
            replyMsgVO.setBody(MsgHandlerFactory.getStrategyNoNull(replyMessage.getType()).showReplyMsg(replyMessage));
            User replyUser = userCache.getUserInfo(replyMessage.getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
            replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount()) && msg.getGapCount() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
            replyMsgVO.setGapCount(msg.getGapCount());
            resp.setReply(replyMsgVO);
        }
        return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }
}
