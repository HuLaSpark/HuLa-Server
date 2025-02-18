package com.hula.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.user.domain.entity.UserState;
import com.hula.core.user.mapper.UserStateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户在线状态表
 * </p>
 *
 * @author 乾乾
 */
@Service
public class UserStateDao extends ServiceImpl<UserStateMapper, UserState> {

	public List<UserState> list(){
		// TODO 应当加上缓存
		return lambdaQuery()
				.list();
	}
}
