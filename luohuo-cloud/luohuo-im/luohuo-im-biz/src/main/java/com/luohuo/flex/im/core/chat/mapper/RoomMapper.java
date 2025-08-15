package com.luohuo.flex.im.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.flex.im.domain.vo.res.GroupListVO;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.Room;

/**
 * <p>
 * 房间表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
@Repository
public interface RoomMapper extends BaseMapper<Room> {

    IPage<GroupListVO> groupList(Long uid, IPage<GroupListVO> page);
}
