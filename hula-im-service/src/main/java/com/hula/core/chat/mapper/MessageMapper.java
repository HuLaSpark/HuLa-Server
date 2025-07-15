package com.hula.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.core.chat.domain.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 消息表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
public interface MessageMapper extends BaseMapper<Message> {

	@Select({
			"<script>",
			"SELECT * FROM message WHERE room_id IN",
			"<foreach item='roomId' collection='roomIds' open='(' separator=',' close=')'>",
			"#{roomId}",
			"</foreach>",
			"AND id > COALESCE(#{lastMsgIds[roomId]}, 0)",
			"ORDER BY create_time DESC",
			"</script>"
	})
	List<Message> selectMessagesByRoomIds(@Param("roomIds") List<Long> roomIds, @Param("lastMsgIds") Map<Long, Long> lastMsgIds);
}
