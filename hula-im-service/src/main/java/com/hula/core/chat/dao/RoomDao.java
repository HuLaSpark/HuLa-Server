package com.hula.core.chat.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.core.chat.domain.entity.Room;
import com.hula.core.chat.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> implements IService<Room> {

    public void refreshActiveTime(Long roomId, Long msgId, Date msgTime) {
        lambdaUpdate()
                .eq(Room::getId, roomId)
                .set(Room::getLastMsgId, msgId)
                .set(Room::getActiveTime, msgTime)
                .update();
    }

    public List<GroupListVO> groupList(Long uid, IPage<GroupListVO> page) {
        return baseMapper.groupList(uid,page).getRecords();
    }
}
