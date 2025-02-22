package com.hula.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.user.domain.entity.UserTargetRel;
import com.hula.core.user.mapper.UserTargetRelMapper;
import org.springframework.stereotype.Service;
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
