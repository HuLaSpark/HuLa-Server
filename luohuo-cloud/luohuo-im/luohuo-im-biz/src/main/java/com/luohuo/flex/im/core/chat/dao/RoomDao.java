package com.luohuo.flex.im.core.chat.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.core.chat.mapper.RoomMapper;
import com.luohuo.flex.im.domain.vo.res.GroupListVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void refreshActiveTime(Long roomId, Long msgId, LocalDateTime msgTime) {
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
