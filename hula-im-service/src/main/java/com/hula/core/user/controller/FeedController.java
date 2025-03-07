package com.hula.core.user.controller;

import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.core.user.domain.vo.OperParam;
import com.hula.core.user.domain.vo.req.feed.FeedParam;
import com.hula.core.user.domain.vo.req.feed.FeedPermission;
import com.hula.core.user.domain.vo.req.feed.FeedVo;
import com.hula.core.user.service.FeedService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发布朋友圈、编辑朋友圈、设置谁可见、谁不可见、仅聊天、不看他、不让他看我
 */
@RestController
@RequestMapping("/feed/")
@Api(tags = "朋友圈模块")
public class FeedController {

	@Resource
	private FeedService feedService;

	@PostMapping("list")
	@Operation(summary = "朋友圈列表")
	public ApiResult<CursorPageBaseResp<FeedVo>> list(@Valid @RequestBody CursorPageBaseReq request) {
		return ApiResult.success(feedService.getFeedPage(request, RequestHolder.get().getUid()));
	}

	@PostMapping("pushFeed")
	@Operation(summary = "发布朋友圈")
	public ApiResult<Boolean> pushFeed(@Valid @RequestBody FeedParam param) {
		return ApiResult.success(feedService.pushFeed(RequestHolder.get().getUid(), param));
	}

	@GetMapping("getFeedPermission/{feedId}")
	@Operation(summary = "查看朋友圈权限")
	public ApiResult<FeedPermission> getFeedPermission(@PathVariable("feedId") Long feedId) {
		return ApiResult.success(feedService.getFeedPermission(RequestHolder.get().getUid(), feedId));
	}

	@PostMapping("edit")
	@Operation(summary = "编辑朋友圈")
	public ApiResult<Boolean> editFeed(@Validated(value = {OperParam.Update.class, Default.class}) @RequestBody FeedParam param){
		return ApiResult.success(feedService.editFeed(RequestHolder.get().getUid(), param));
	}

	@PostMapping("del/{feedId}")
	@Operation(summary = "删除朋友圈")
	public ApiResult<Boolean> delFeed(@PathVariable("feedId") Long feedId) {
		return ApiResult.success(feedService.delFeed(feedId));
	}

	@GetMapping("detail/{feedId}")
	@Operation(summary = "用户查看详情")
	public ApiResult<FeedVo> feedDetail(@PathVariable("feedId") Long feedId) {
		return ApiResult.success(feedService.feedDetail(feedId));
	}
}
