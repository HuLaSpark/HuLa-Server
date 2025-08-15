package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.flex.im.common.utils.discover.PrioritizedUrlDiscover;
import com.luohuo.flex.im.domain.UrlInfo;
import com.luohuo.flex.im.common.utils.sensitiveword.SensitiveWordBs;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.entity.msg.NoticeMsgDTO;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 公告消息
 * @author 乾乾
 */
@Component
@AllArgsConstructor
public class NoticeMsgHandler extends AbstractMsgHandler<NoticeMsgDTO> {
    private MessageDao messageDao;
	private SensitiveWordBs sensitiveWordBs;

	private static final PrioritizedUrlDiscover URL_TITLE_DISCOVER = new PrioritizedUrlDiscover();

	@Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.NOTICE;
    }

	@Override
	protected void saveMsg(Message message, NoticeMsgDTO body) {
		MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
		Message update = new Message();
		update.setId(message.getId());
		// 过滤公告消息的某些字符
		body.setContent(sensitiveWordBs.filter(body.getContent()));
		extra.setNoticeMsgDTO(body);
		update.setExtra(extra);
		update.setReplyMsgId(body.getReplyMsgId());
		// 判断消息url跳转
		Map<String, UrlInfo> urlContentMap = URL_TITLE_DISCOVER.getUrlContentMap(body.getContent());
		extra.setUrlContentMap(urlContentMap);
		messageDao.updateById(update);
	}

	@Override
    public Object showMsg(Message msg) {
		NoticeMsgDTO resp = msg.getExtra().getNoticeMsgDTO();
		resp.setReply(replyMsg(msg));
		return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
		return "公告";
    }

    @Override
    public String showContactMsg(Message msg) {
		return "[公告]";
    }
}
