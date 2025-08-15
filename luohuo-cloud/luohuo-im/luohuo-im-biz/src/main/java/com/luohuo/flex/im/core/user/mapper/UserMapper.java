package com.luohuo.flex.im.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.domain.entity.User;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 乾乾
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

	int changeUserState(@Param("employeeId") Long employeeId, @Param("userStateId") Long userStateId);

	List<ChatMemberListResp> getFriend(@Param("key") String key);
}
