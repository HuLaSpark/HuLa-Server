package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.domain.entity.UserTargetRel;
import com.luohuo.flex.im.core.user.mapper.UserTargetRelMapper;

import java.util.List;

/**
 * <p>
 * 用户标签关联表 服务实现类
 * </p>
 *
 * @author 乾乾
 */
@Service
public class UserTargetRelDao extends ServiceImpl<UserTargetRelMapper, UserTargetRel> {

	public List<Long> getRelTarget(Long uid, Long friendId) {
		return lambdaQuery().select(UserTargetRel::getId)
				.eq(UserTargetRel::getUid, uid)
				.eq(UserTargetRel::getFriendId, friendId)
				.list().stream().map(UserTargetRel::getId).toList();
	}
}
