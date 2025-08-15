package com.luohuo.flex.im.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;

@Repository
public interface FeedMapper extends BaseMapper<Feed> {

	FeedVo getDetail(Long id);
}
