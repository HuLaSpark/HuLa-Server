package com.hula.core.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.chat.domain.entity.GroupMember;
import com.hula.core.chat.domain.enums.GroupRoleEnum;
import com.hula.core.chat.mapper.GroupMemberMapper;
import com.hula.core.chat.service.cache.GroupMemberCache;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hula.core.chat.domain.enums.GroupRoleEnum.ADMIN_LIST;


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

    public List<Long> getMemberBatch(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    /**
     * 批量获取成员群角色
     *
     * @param groupId 群ID
     * @param uidList 用户列表
     * @return 成员群角色列表
     */
    public Map<Long, Integer> getMemberMapRole(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .in(GroupMember::getRole, ADMIN_LIST)
                .select(GroupMember::getUid, GroupMember::getRole)
                .list();
        return list.stream().collect(Collectors.toMap(GroupMember::getUid, GroupMember::getRole));
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
			wrapper.in(GroupMember::getRole, GroupRoleEnum.MANAGER.getType(), GroupRoleEnum.LEADER.getType());
		}
		return
				wrapper.select(GroupMember::getUid)
				.eq(GroupMember::getGroupId, id)
				.in(GroupMember::getRole, GroupRoleEnum.MANAGER.getType(), GroupRoleEnum.LEADER.getType()).list().stream().map(GroupMember::getUid).toList();
	}

	/**
	 * 查询人员在群里的角色
	 * @param groupId
	 * @param uid
	 * @return
	 */
    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
    }

    public List<GroupMember> getSelfGroup(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
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
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
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
                .eq(GroupMember::getRole, GroupRoleEnum.MANAGER.getType())
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
                .eq(GroupMember::getRole, GroupRoleEnum.MANAGER.getType())
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
                .set(GroupMember::getRole, GroupRoleEnum.MANAGER.getType());
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
                .set(GroupMember::getRole, GroupRoleEnum.MEMBER.getType());
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
        if (CollectionUtil.isNotEmpty(uidList)) {
            LambdaQueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>()
                    .lambda()
                    .eq(GroupMember::getGroupId, groupId)
                    .in(GroupMember::getUid, uidList);
            return this.remove(wrapper);
        }
        return false;
    }

	/**
	 * 将群员设置为屏蔽该群
	 * @param roomId
	 * @param uid
	 */
	public void setMemberDeFriend(Long roomId, Long uid, Boolean deFriend) {
		GroupMember member = getMember(roomId, uid);
		member.setDeFriend(deFriend);
		updateById(member);
		groupMemberCache.evictMemberUidList(roomId);
	}
}
