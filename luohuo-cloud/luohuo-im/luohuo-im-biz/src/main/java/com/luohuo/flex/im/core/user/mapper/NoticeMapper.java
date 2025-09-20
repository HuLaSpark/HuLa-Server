package com.luohuo.flex.im.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.im.domain.entity.Notice;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendUnreadDto;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <p>
 * 通知 Mapper 接口
 * </p>
 *
 * @author 乾乾
 */
@Repository
public interface NoticeMapper extends BaseMapper<Notice> {

	List<FriendUnreadDto> getUnReadCountByType(Long receiverId, Integer isRead);
}
