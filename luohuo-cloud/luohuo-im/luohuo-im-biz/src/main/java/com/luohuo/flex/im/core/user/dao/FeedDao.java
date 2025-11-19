package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;
import com.luohuo.flex.im.core.user.mapper.FeedMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 朋友圈基础信息
 */
@Service
public class FeedDao extends ServiceImpl<FeedMapper, Feed> {

	/**
	 * 查询朋友圈列表（包括指定用户列表的朋友圈）
	 *
	 * @param uidList 用户ID列表（包括当前用户和好友）
	 * @param request 分页请求
	 * @return 朋友圈分页结果
	 */
	public CursorPageBaseResp<Feed> getFeedPage(List<Long> uidList, CursorPageBaseReq request) {
		return CursorUtils.getCursorPageByMysql(this, request, wrapper -> wrapper.in(Feed::getUid, uidList), Feed::getCreateTime);
	}

	public FeedVo getDetail(Long id) {
		return baseMapper.getDetail(id);
	}
}
