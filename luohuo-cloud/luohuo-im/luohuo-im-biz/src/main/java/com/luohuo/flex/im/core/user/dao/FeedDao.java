package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;
import com.luohuo.flex.im.core.user.mapper.FeedMapper;
import org.springframework.stereotype.Service;

/**
 * 朋友圈基础信息
 */
@Service
public class FeedDao extends ServiceImpl<FeedMapper, Feed> {

	public CursorPageBaseResp<Feed> getFeedPage(Long uid, CursorPageBaseReq request) {
		return CursorUtils.getCursorPageByMysql(this, request, wrapper -> wrapper.eq(Feed::getUid, uid), Feed::getCreateTime);
	}

	public FeedVo getDetail(Long id) {
		return baseMapper.getDetail(id);
	}
}
