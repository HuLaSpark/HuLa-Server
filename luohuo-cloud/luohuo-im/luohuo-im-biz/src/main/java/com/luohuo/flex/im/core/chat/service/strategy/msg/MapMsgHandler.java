package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.im.domain.vo.response.msg.MapMsgDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 地图消息
 * @author 乾乾
 */
@Component
@AllArgsConstructor
public class MapMsgHandler extends AbstractMsgHandler<MapMsgDTO> {

    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.LOCATION;
    }

    @Override
    public void saveMsg(Message msg, MapMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setContent(body.getAddress());
        update.setExtra(extra);

		// 填充地图信息
		MapMsgDTO mapMsgDTO = new MapMsgDTO();
		mapMsgDTO.setTimestamp(body.getTimestamp());
		mapMsgDTO.setAddress(body.getAddress());
		mapMsgDTO.setLongitude(body.getLongitude());
		mapMsgDTO.setLatitude(body.getLatitude());
		mapMsgDTO.setPrecision(body.getPrecision());
		extra.setMapMsgDTO(mapMsgDTO);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getMapMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
		return "[位置信息]";
    }

    @Override
    public String showContactMsg(Message msg) {
		return "[位置信息]";
    }
}
