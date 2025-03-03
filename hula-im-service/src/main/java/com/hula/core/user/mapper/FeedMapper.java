package com.hula.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.core.user.domain.entity.Feed;
import com.hula.core.user.domain.vo.req.feed.FeedVo;

public interface FeedMapper extends BaseMapper<Feed> {

	FeedVo getDetail(Long id);
}
