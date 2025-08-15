package com.luohuo.flex.im.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.model.vo.query.OperParam;
import com.luohuo.flex.im.domain.vo.req.feed.FeedParam;
import com.luohuo.flex.im.domain.vo.req.feed.FeedPermission;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;
import com.luohuo.flex.im.core.user.service.FeedService;
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
@Tag(name = "朋友圈模块")
public class FeedController {

	@Resource
	private FeedService feedService;

	@PostMapping("list")
	@Operation(summary = "朋友圈列表")
	public R<CursorPageBaseResp<FeedVo>> list(@Valid @RequestBody CursorPageBaseReq request) {
		return R.success(feedService.getFeedPage(request, ContextUtil.getUid()));
	}

	@PostMapping("pushFeed")
	@Operation(summary = "发布朋友圈")
	public R<Boolean> pushFeed(@Valid @RequestBody FeedParam param) {
		return R.success(feedService.pushFeed(ContextUtil.getUid(), param));
	}

	@GetMapping("getFeedPermission/{feedId}")
	@Operation(summary = "查看朋友圈权限")
	public R<FeedPermission> getFeedPermission(@PathVariable("feedId") Long feedId) {
		return R.success(feedService.getFeedPermission(ContextUtil.getUid(), feedId));
	}

	@PostMapping("edit")
	@Operation(summary = "编辑朋友圈")
	public R<Boolean> editFeed(@Validated(value = {OperParam.Update.class, Default.class}) @RequestBody FeedParam param){
		return R.success(feedService.editFeed(ContextUtil.getUid(), param));
	}

	@PostMapping("del/{feedId}")
	@Operation(summary = "删除朋友圈")
	public R<Boolean> delFeed(@PathVariable("feedId") Long feedId) {
		return R.success(feedService.delFeed(feedId));
	}

	@GetMapping("detail/{feedId}")
	@Operation(summary = "用户查看详情")
	public R<FeedVo> feedDetail(@PathVariable("feedId") Long feedId) {
		return R.success(feedService.feedDetail(feedId));
	}
}
