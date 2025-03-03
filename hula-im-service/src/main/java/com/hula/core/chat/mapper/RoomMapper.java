package com.hula.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.core.chat.domain.entity.Room;

/**
 * <p>
 * 房间表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
public interface RoomMapper extends BaseMapper<Room> {

    IPage<GroupListVO> groupList(Long uid, IPage<GroupListVO> page);
}
