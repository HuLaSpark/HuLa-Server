package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.domain.entity.UserFriend;
import com.luohuo.flex.im.core.user.mapper.UserFriendMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {
	public List<UserFriend> getByFriends(Long uid, Collection<Long> uidList) {
		return lambdaQuery().eq(UserFriend::getUid, uid)
				.in(UserFriend::getFriendUid, uidList)
				.list();
	}

	public UserFriend getByFriend(Long uid, Long targetUid) {
		return lambdaQuery().eq(UserFriend::getUid, uid)
				.eq(UserFriend::getFriendUid, targetUid)
				.one();
	}

	public CursorPageBaseResp<UserFriend> getFriendPage(Long uid, CursorPageBaseReq cursorPageBaseReq) {
		return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq,
				wrapper -> wrapper.eq(UserFriend::getUid, uid), UserFriend::getId);
	}

	public List<UserFriend> getUserFriend(Long uid, Long friendUid) {
		return lambdaQuery()
				.eq(UserFriend::getUid, uid)
				.eq(UserFriend::getFriendUid, friendUid)
				.or()
				.eq(UserFriend::getFriendUid, uid)
				.eq(UserFriend::getUid, friendUid)
				.select(UserFriend::getId, UserFriend::getRoomId)
				.list();
	}

	/**
	 * 查询他不看我的好友
	 *
	 * @param uid 操作人
	 * @return
	 */
	public List<Long> getHideLookMe(Long uid) {
		return baseMapper.selectObjs(new LambdaQueryWrapper<UserFriend>()
						.select(UserFriend::getUid)
						.eq(UserFriend::getFriendUid, uid)
						.eq(UserFriend::getHideTheirPosts, true))
				.stream()
				.map(o -> (Long) o)
				.collect(Collectors.toList());
	}

	/**
	 * 查询不让他看我的好友
	 *
	 * @param uid 操作人
	 * @return
	 */
	public List<Long> getHideMyPosts(Long uid) {
		return baseMapper.selectObjs(new LambdaQueryWrapper<UserFriend>()
						.select(UserFriend::getFriendUid)
						.eq(UserFriend::getUid, uid)
						.eq(UserFriend::getHideMyPosts, true))
				.stream()
				.map(o -> (Long) o)
				.collect(Collectors.toList());
	}

	/**
	 * 查询我不看他的好友
	 *
	 * @param uid 操作人
	 * @return
	 */
	public List<Long> getHideTheirPosts(Long uid) {
		return baseMapper.selectObjs(new LambdaQueryWrapper<UserFriend>()
						.select(UserFriend::getFriendUid)
						.eq(UserFriend::getUid, uid)
						.eq(UserFriend::getHideTheirPosts, true))
				.stream()
				.map(o -> (Long) o)
				.collect(Collectors.toList());
	}

	/**
	 * 查询仅聊天的好友
	 *
	 * @param uid 操作人
	 * @return
	 */
	public List<Long> getJustChat(Long uid) {
		return baseMapper.selectObjs(new LambdaQueryWrapper<UserFriend>()
						.select(UserFriend::getFriendUid)
						.eq(UserFriend::getUid, uid)
						.eq(UserFriend::getHideMyPosts, true)
						.eq(UserFriend::getHideTheirPosts, true))
				.stream()
				.map(o -> (Long) o)
				.collect(Collectors.toList());
	}

	public List<Long> getAllRoomIdsByUid(Long uid) {
		return lambdaQuery()
				.eq(UserFriend::getUid, uid)
				.list().stream().map(UserFriend::getRoomId).collect(Collectors.toList());
	}

	/**
	 * TODO 这里也要重构
	 * 获取到当前登录人员的所有的好友的信息
	 *
	 * @return
	 */
	@Cacheable(cacheNames = "luohuo:friend", key = "'list:'+#uid")
	public List<Long> getAllFriendIdsByUid(Long uid) {
		LambdaQueryWrapper<UserFriend> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.select(UserFriend::getFriendUid)
				.eq(UserFriend::getUid, uid);
		return baseMapper.selectObjs(queryWrapper).stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	/**
	 * 根据房间号+自己的id 定位好友关系
	 *
	 * @param roomId
	 * @param uid
	 * @return
	 */
	@Cacheable(cacheNames = "luohuo:userFriend", key = "'room:'+#roomId+':uid:'+#uid", unless = "#result == null")
	public UserFriend getByRoomId(Long roomId, Long uid) {
		return lambdaQuery()
				.eq(UserFriend::getRoomId, roomId)
				.eq(UserFriend::getUid, uid)
				.one();
	}

	public UserFriend getByFriend2(Long uid1, Long uid2) {
		return lambdaQuery()
				.eq(UserFriend::getUid, uid1)
				.eq(UserFriend::getFriendUid, uid2)
				.one();
	}

	/**
	 * 当好友关系变更时清除缓存
	 *
	 * @param roomId 房间ID
	 * @param uid    用户ID
	 */
	@CacheEvict(cacheNames = "luohuo:userFriend", key = "'room:'+#roomId+':uid:'+#uid")
	public void evictFriendCache(Long roomId, Long uid) {
	}

	/**
	 * 查询所有好友房间
	 *
	 * @param roomIdList 房间id
	 */
	public List<UserFriend> getAllRoomInfo(List<Long> roomIdList) {
		return lambdaQuery()
				.in(UserFriend::getRoomId, roomIdList)
				.list();
	}
}
