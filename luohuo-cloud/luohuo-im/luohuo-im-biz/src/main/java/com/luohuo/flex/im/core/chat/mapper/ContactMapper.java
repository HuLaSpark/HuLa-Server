package com.luohuo.flex.im.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.Contact;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 会话列表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
@Repository
public interface ContactMapper extends BaseMapper<Contact> {

    void refreshOrCreateActiveTime(@Param("roomId") Long roomId, @Param("memberUidList") List<Long> memberUidList, @Param("msgId") Long msgId, @Param("activeTime") LocalDateTime activeTime);

	/**
	 * 刷新会话状态，ack专用
	 */
	void refreshOrCreateActive(@Param("roomId") Object roomId, @Param("memberUidList") List<Long> memberUidList, @Param("msgId") Object msgId, @Param("activeTime") Object activeTime);

	/**
	 * 刷新或创建会话
	 * @param roomId 房间id
	 * @param uid 用户id
	 */
	void refreshOrCreate(@Param("roomId") Long roomId, @Param("uid") Long uid);
}
