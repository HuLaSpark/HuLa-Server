package com.luohuo.flex.im.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.im.domain.vo.response.MemberResp;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.Room;

import java.util.List;

/**
 * <p>
 * 房间表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
@Repository
public interface RoomMapper extends BaseMapper<Room> {

    List<MemberResp> groupList(Long uid);

    List<MemberResp> getAllGroupList();
}
