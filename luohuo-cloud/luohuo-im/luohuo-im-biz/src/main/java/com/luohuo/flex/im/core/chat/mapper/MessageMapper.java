package com.luohuo.flex.im.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.im.domain.entity.Contact;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.Message;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 消息表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
@Repository
public interface MessageMapper extends BaseMapper<Message> {

	@MapKey("room_id")
	Map<Long, Integer> batchGetUnReadCount(Collection<Contact> contactList);
}
