package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.common.enums.NormalOrNoEnum;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.core.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getOpenId, openId);
        return getOne(wrapper);
    }

    public void modifyName(Long uid, String name) {
        User update = new User();
        update.setId(uid);
        update.setName(name);
        updateById(update);
    }

    public void wearingBadge(Long uid, Long badgeId) {
        User update = new User();
        update.setId(uid);
        update.setItemId(badgeId);
        updateById(update);
    }

	public List<User> getByDefUserId(List<Long> defUserIdLIst) {
		return lambdaQuery().in(User::getUserId, defUserIdLIst).list();
	}

    public User getByEmail(String email) {
        return lambdaQuery().eq(User::getEmail, email).one();
    }

    public List<User> getMemberList() {
        return lambdaQuery()
                .eq(User::getState, NormalOrNoEnum.NORMAL.getStatus())
                //最近活跃的1000个人，可以用lastOptTime字段，但是该字段没索引，updateTime可平替
                .orderByDesc(User::getLastOptTime)
                //毕竟是大群聊，人数需要做个限制
                .last("limit 1000")
                .select(User::getId, User::getName, User::getAvatar, User::getAccount)
                .list();

    }

	/**
	 * @param memberUidList 在线或离线的群成员id
	 */
    public CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq request) {
		if(memberUidList == null || memberUidList.size() == 0){
			return new CursorPageBaseResp<>();
		}
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.in(CollectionUtils.isNotEmpty(memberUidList), User::getId, memberUidList);//普通群对uid列表做限制
        }, User::getLastOptTime);
    }

    public int changeUserState(Long uid, Long userStateId) {
        return baseMapper.changeUserState(uid, userStateId);
    }

    public List<ChatMemberListResp> getFriend(String key) {
        return baseMapper.getFriend("%" + key + "%");
    }

    public Boolean existsByEmailAndIdNot(Long uid, String email) {
        LambdaQueryWrapper<User> wrapper =  new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email);

        if(uid != null){
            wrapper.ne(User::getId, uid);
        }
        return baseMapper.selectCount(wrapper) > 0;
    }

    public List<User> getByIds(Set<Long> uidSet) {
        return baseMapper.selectBatchIds(uidSet);
    }
}
