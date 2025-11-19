package com.luohuo.flex.im.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.im.domain.vo.response.GroupResp;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.RoomGroup;

/**
 * <p>
 * 群聊房间表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
@Repository
public interface RoomGroupMapper extends BaseMapper<RoomGroup> {

    boolean checkUser(Long uid, Long roomId);

	GroupResp getByRoomIdIgnoreDel(Long roomId);
}
