package com.luohuo.flex.im.core.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.core.chat.service.cache.RoomGroupCache;
import com.luohuo.flex.im.domain.entity.GroupMember;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.core.chat.mapper.GroupMemberMapper;
import com.luohuo.flex.im.core.chat.service.cache.GroupMemberCache;
import com.luohuo.flex.model.entity.ws.ChatMember;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    @Resource
    @Lazy
    private GroupMemberCache groupMemberCache;
	@Resource
	private RoomGroupCache roomGroupCache;

	/**
	 * 查询群成员
	 * deFriend true -> 查询屏蔽的  false -> 不查屏蔽群的人
	 * @return
	 */
    public List<Long> getMemberUidList(Long groupId, Boolean deFriend) {
		LambdaQueryChainWrapper<GroupMember> wrapper = lambdaQuery()
				.eq(GroupMember::getGroupId, groupId)
				.select(GroupMember::getUid);
		if(ObjectUtil.isNotNull(deFriend)){
			wrapper.eq(GroupMember::getDeFriend, deFriend);
		}
		return wrapper.list().stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    public List<GroupMember> getMemberBatch(Long groupId, Collection<Long> uidList) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .list();
    }

	/**
	 * 获取群聊人员
	 * isSpecial：true -> 获取群主、管理员
	 * isSpecial：false -> 获取所有人员
	 * @param id 群id
	 * @return
	 */
	public List<Long> getGroupUsers(Long id, Boolean isSpecial) {
		LambdaQueryChainWrapper<GroupMember> wrapper = lambdaQuery();
		if (isSpecial){
			wrapper.in(GroupMember::getRoleId, GroupRoleEnum.MANAGER.getType(), GroupRoleEnum.LEADER.getType());
		} else {
			wrapper.in(GroupMember::getRoleId, GroupRoleEnum.MANAGER.getType(), GroupRoleEnum.LEADER.getType(), GroupRoleEnum.MEMBER.getType());
		}
		return wrapper.select(GroupMember::getUid).eq(GroupMember::getGroupId, id).list().stream().map(GroupMember::getUid).toList();
	}

	/**
	 * 查询人员在群里的角色
	 * @param roomId 房间id
	 * @param uid 用户id
	 * @return
	 */
    public GroupMember getMember(Long roomId, Long uid) {
		RoomGroup roomGroup = roomGroupCache.getByRoomId(roomId);
		return lambdaQuery()
                .eq(GroupMember::getGroupId, roomGroup.getId())
                .eq(GroupMember::getUid, uid)
                .one();
    }

	public GroupMember getMemberByGroupId(Long groupId, Long uid) {
		return lambdaQuery()
				.eq(GroupMember::getGroupId, groupId)
				.eq(GroupMember::getUid, uid)
				.one();
	}

	/**
	 * 查询自己创建的群聊数量
	 * @param uid 当前用户
	 */
    public List<GroupMember> getSelfGroup(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRoleId, GroupRoleEnum.LEADER.getType())
                .list();
    }

    /**
     * 判断用户是否在房间中
     *
     * @param roomId  房间ID
     * @param uidList 用户ID
     * @return 是否在群聊中
     */
    public Boolean isGroupShip(Long roomId, List<Long> uidList) {
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        return memberUidList.containsAll(uidList);
    }

    /**
     * 是否是群主
     *
     * @param id  群组ID
     * @param uid 用户ID
     * @return 是否是群主
     */
    public Boolean isLord(Long id, Long uid) {
        GroupMember groupMember = this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRoleId, GroupRoleEnum.LEADER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * 是否是管理员
     *
     * @param id  群组ID
     * @param uid 用户ID
     * @return 是否是管理员
     */
    public Boolean isManager(Long id, Long uid) {
        GroupMember groupMember = this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRoleId, GroupRoleEnum.MANAGER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * 获取管理员uid列表
     *
     * @param id 群主ID
     * @return 管理员uid列表
     */
    public List<Long> getManageUidList(Long id) {
        return this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getRoleId, GroupRoleEnum.MANAGER.getType())
                .list()
                .stream()
                .map(GroupMember::getUid)
                .collect(Collectors.toList());
    }

    /**
     * 增加管理员
     *
     * @param id      群组ID
     * @param uidList 用户列表
     */
    public void addAdmin(Long id, List<Long> uidList) {
        LambdaUpdateWrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>().lambda()
                .eq(GroupMember::getGroupId, id)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRoleId, GroupRoleEnum.MANAGER.getType());
        this.update(wrapper);
    }

    /**
     * 撤销管理员
     *
     * @param id      群组ID
     * @param uidList 用户列表
     */
    public void revokeAdmin(Long id, List<Long> uidList) {
        LambdaUpdateWrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>().lambda()
                .eq(GroupMember::getGroupId, id)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRoleId, GroupRoleEnum.MEMBER.getType());
        this.update(wrapper);
    }

    /**
     * 根据群组ID删除群成员
     *
     * @param groupId 群组ID
     * @param uidList 群成员列表
     * @return 是否删除成功
     */
    public Boolean removeByGroupId(Long groupId, List<Long> uidList) {
		LambdaQueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>().lambda().eq(GroupMember::getGroupId, groupId);
        if (CollectionUtil.isNotEmpty(uidList)) {
			wrapper.in(GroupMember::getUid, uidList);
        }
        return this.remove(wrapper);
    }

	/**
	 * 将群员设置为屏蔽该群
	 * @param roomId 房间id
	 * @param uid 屏蔽的人
	 */
	public void setMemberDeFriend(Long roomId, Long uid, Boolean deFriend) {
		GroupMember member = getMember(roomId, uid);
		member.setDeFriend(deFriend);
		updateById(member);
		groupMemberCache.evictExceptMemberList(roomId);
		groupMemberCache.evictMemberDetail(roomId, uid);
	}

	public List<ChatMemberResp> getMemberListByGroupId(Long groupId) {
		return baseMapper.getMemberListByGroupId(groupId);
	}

	public List<ChatMember> getMemberListByUid(List<Long> memberList) {
		return baseMapper.getMemberListByUid(memberList);
	}

	public List<GroupMember> getGroupMemberByGroupIdListAndUid(Long uid, Set<Long> groupIdList) {
		QueryWrapper<GroupMember> wrapper = new QueryWrapper<>();
		wrapper.in("group_id", groupIdList).eq("uid", uid);
		return baseMapper.selectList(wrapper);
	}
}
