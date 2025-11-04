package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.UserEmoji;
import com.luohuo.flex.im.core.user.mapper.UserEmojiMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class UserEmojiDao extends ServiceImpl<UserEmojiMapper, UserEmoji> {

	public List<UserEmoji> listByUid(Long uid) {
		return lambdaQuery().eq(UserEmoji::getUid, uid).list();
	}

	public int countByUid(Long uid) {
		return Math.toIntExact(lambdaQuery().eq(UserEmoji::getUid, uid).count());
	}
}
