package com.luohuo.flex.im.core.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.core.chat.mapper.RoomGroupMapper;
import com.luohuo.flex.im.domain.vo.response.GroupResp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    public List<RoomGroup> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, roomIds)
                .list();
    }

    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
    }

	public GroupResp getByRoomIdIgnoreDel(Long roomId) {
		return baseMapper.getByRoomIdIgnoreDel(roomId);
	}

    public boolean checkUser(Long uid, Long roomId) {
        return baseMapper.checkUser(uid,roomId);
    }

	public List<RoomGroup> searchGroup(String account) {
		return lambdaQuery()
				.like(RoomGroup::getAccount, account)
				.list();
	}

	public RoomGroup searchGroupByAccount(String account) {
		return lambdaQuery()
				.eq(RoomGroup::getAccount, account)
				.one();
	}

	public List<Long> getRoomIdByGroupId(List<Long> groupIds) {
		return lambdaQuery()
				.in(RoomGroup::getId, groupIds)
				.list().stream().map(RoomGroup::getRoomId).collect(Collectors.toList());
	}
}
