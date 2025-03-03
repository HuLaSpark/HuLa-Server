package com.hula.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.utils.CursorUtils;
import com.hula.core.user.domain.entity.UserFriend;
import com.hula.core.user.mapper.UserFriendMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
    public List<UserFriend> getByFriends(Long uid, List<Long> uidList) {
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
                .select(UserFriend::getId)
                .list();
    }

	/**
	 * 查询他不看我的好友
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

	/**
	 * 获取到当前登录人员的所有的好友的信息
	 * @return
	 */
	@Cacheable(cacheNames = "user", key = "'friends'+#uid")
	public List<Long> getAllFriendIdsByUid(Long uid){
		LambdaQueryWrapper<UserFriend> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.select(UserFriend::getFriendUid)
				.eq(UserFriend::getUid, uid);
		return baseMapper.selectObjs(queryWrapper).stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	/**
	 * 当uid的朋友改变后需要调用此方法
	 * @param uid
	 * @return
	 */
	@CacheEvict(cacheNames = "user", key = "'findGroup'+#uid")
	public List<Long> evictGroup(Long uid) {
		return null;
	}
}
