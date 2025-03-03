package com.hula.core.user.domain.vo.req.feed;

import com.hula.core.user.domain.entity.Feed;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 发布朋友圈的返回体
 */
@Data
public class FeedVo extends Feed {

	@Schema(description = "发布的内容的url")
	private List<String> urls;
}
