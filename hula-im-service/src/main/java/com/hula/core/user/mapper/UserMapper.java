package com.hula.core.user.mapper;

import com.hula.core.user.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 乾乾
 */
public interface UserMapper extends BaseMapper<User> {

	int changeUserState(@Param("employeeId") Long employeeId, @Param("userStateId") Long userStateId);
}
