package com.hula.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.utils.CursorUtils;
import com.hula.core.user.domain.entity.Feed;
import com.hula.core.user.domain.vo.req.feed.FeedVo;
import com.hula.core.user.mapper.FeedMapper;
import org.springframework.stereotype.Service;

/**
 * 朋友圈基础信息
 */
@Service
public class FeedDao extends ServiceImpl<FeedMapper, Feed> {

	public CursorPageBaseResp<Feed> getFeedPage(Long uid, CursorPageBaseReq request) {
		return CursorUtils.getCursorPageByMysql(this, request, wrapper -> wrapper.eq(Feed::getUid, uid), Feed::getCreatedTime);
	}

	public FeedVo getDetail(Long id) {
		return baseMapper.getDetail(id);
	}
}
