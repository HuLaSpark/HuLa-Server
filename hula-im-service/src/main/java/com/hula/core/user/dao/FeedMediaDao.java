package com.hula.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.user.domain.entity.FeedMedia;
import com.hula.core.user.mapper.FeedMediaMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 朋友圈素材
 */
@Service
public class FeedMediaDao extends ServiceImpl<FeedMediaMapper, FeedMedia> {

	/**
	 * 批量添加朋友圈的资源的数据
	 * @param feedId 朋友圈id
	 * @param urls 素材地址
	 * @param type 0 纯文字 1 图片 2 视频
	 */
	public List<FeedMedia> batchSaveMedia(Long feedId, List<String> urls, Integer type){
		List<FeedMedia> feedMediaList = new ArrayList<>();
		for (int i = 0; i < urls.size(); i++) {
			String url = urls.get(i);
			FeedMedia feedMedia = new FeedMedia();
			feedMedia.setSort(i);
			feedMedia.setFeedId(feedId);
			feedMedia.setUrl(url);
			feedMediaList.add(feedMedia);
		}
		saveBatch(feedMediaList);
		return feedMediaList;
	}

	/**
	 * 删除朋友圈的资源等消息
	 * @param feedId
	 * @return
	 */
	public boolean delMediaByFeedId(Long feedId) {
		return remove(new LambdaQueryWrapper<FeedMedia>().eq(FeedMedia::getFeedId, feedId));
	}

	/**
	 * 通过id获取到朋友圈资源信息
	 * @param feedId
	 * @return
	 */
	public List<FeedMedia> getMediaByFeedId(Long feedId) {
		return list(new LambdaQueryWrapper<FeedMedia>().eq(FeedMedia::getFeedId, feedId));
	}
}
