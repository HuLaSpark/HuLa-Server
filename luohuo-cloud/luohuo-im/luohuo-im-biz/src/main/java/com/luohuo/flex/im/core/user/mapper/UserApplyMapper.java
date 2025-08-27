package com.luohuo.flex.im.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendUnreadDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.UserApply;

import java.util.List;

/**
 * <p>
 * 用户申请表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
@Repository
public interface UserApplyMapper extends BaseMapper<UserApply> {

	List<FriendUnreadDto> getUnReadCountByType(@Param("targetId") Long targetId, @Param("readStatus") Integer readStatus, @Param("normal") Integer normal);
}
