package com.hula.core.chat.service.impl;

import com.hula.utils.AssertUtil;
import com.hula.core.chat.dao.ContactDao;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.dto.MsgReadInfoDTO;
import com.hula.core.chat.domain.entity.Contact;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.service.ContactService;
import com.hula.core.chat.service.adapter.ChatAdapter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ContactServiceImpl implements ContactService {

    @Resource
    private ContactDao contactDao;
    @Resource
    private MessageDao messageDao;

    @Override
    public Contact createContact(Long uid, Long roomId) {
        Contact contact = contactDao.get(uid, roomId);
        if (Objects.isNull(contact)) {
            contact = ChatAdapter.buildContact(uid, roomId);
            contactDao.save(contact);
        }
        return contact;
    }

    @Override
    public Integer getMsgReadCount(Message message) {
        return contactDao.getReadCount(message);
    }

    @Override
    public Integer getMsgUnReadCount(Message message) {
        return contactDao.getUnReadCount(message);
    }

    @Override
    public Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages) {
        Map<Long, List<Message>> roomGroup = messages.stream().collect(Collectors.groupingBy(Message::getRoomId));
        AssertUtil.equal(roomGroup.size(), 1, "只能查相同房间下的消息");
        Long roomId = roomGroup.keySet().iterator().next();
        Integer totalCount = contactDao.getTotalCount(roomId);
        return messages.stream().map(message -> {
            MsgReadInfoDTO readInfoDTO = new MsgReadInfoDTO();
            readInfoDTO.setMsgId(message.getId());
            Integer readCount = contactDao.getReadCount(message);
            readInfoDTO.setReadCount(readCount);
            readInfoDTO.setUnReadCount(totalCount - readCount - 1);
            return readInfoDTO;
        }).collect(Collectors.toMap(MsgReadInfoDTO::getMsgId, Function.identity()));
    }
}
