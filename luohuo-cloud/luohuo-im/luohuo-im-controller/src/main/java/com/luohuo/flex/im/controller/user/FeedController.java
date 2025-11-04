package com.luohuo.flex.im.controller.user;

import com.luohuo.flex.im.domain.vo.req.feed.FeedCommentPageReq;
import com.luohuo.flex.im.domain.vo.req.feed.FeedCommentReq;
import com.luohuo.flex.im.domain.vo.req.feed.FeedLikeReq;
import com.luohuo.flex.im.domain.vo.req.feed.FeedReq;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedCommentVo;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedLikeVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.model.vo.query.OperParam;
import com.luohuo.flex.im.domain.vo.req.feed.FeedParam;
import com.luohuo.flex.im.domain.vo.req.feed.FeedPermission;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;
import com.luohuo.flex.im.core.user.service.FeedCommentService;
import com.luohuo.flex.im.core.user.service.FeedLikeService;
import com.luohuo.flex.im.core.user.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 发布朋友圈、编辑朋友圈、设置谁可见、谁不可见、仅聊天、不看他、不让他看我
 */
@RestController
@RequestMapping("/feed/")
@Tag(name = "朋友圈模块")
public class FeedController {

	@Resource
	private FeedService feedService;

	@Resource
	private FeedCommentService feedCommentService;

	@Resource
	private FeedLikeService feedLikeService;

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

	@GetMapping("getFeedPermission")
	@Operation(summary = "查看朋友圈权限")
	public R<FeedPermission> getFeedPermission(FeedReq feedReq) {
		return R.success(feedService.getFeedPermission(ContextUtil.getUid(), feedReq.getFeedId()));
	}

	@PostMapping("edit")
	@Operation(summary = "编辑朋友圈")
	public R<Boolean> editFeed(@Validated(value = {OperParam.Update.class, Default.class}) @RequestBody FeedParam param){
		return R.success(feedService.editFeed(ContextUtil.getUid(), param));
	}

	@PostMapping("del")
	@Operation(summary = "删除朋友圈")
	public R<Boolean> delFeed(@RequestBody FeedReq feedReq) {
		return R.success(feedService.delFeed(feedReq.getFeedId()));
	}

	@GetMapping("detail")
	@Operation(summary = "用户查看详情")
	public R<FeedVo> feedDetail(FeedReq feedReq) {
		return R.success(feedService.feedDetail(feedReq.getFeedId(), ContextUtil.getUid()));
	}

	// ==================== 评论相关接口 ====================

	@PostMapping("comment/add")
	@Operation(summary = "发表评论")
	public R<Boolean> addComment(@Valid @RequestBody FeedCommentReq req) {
		return R.success(feedCommentService.addComment(ContextUtil.getUid(), req));
	}

	@PostMapping("comment/delete")
	@Operation(summary = "删除评论")
	public R<Boolean> delComment(@RequestParam(value = "commentId", required = false) Long commentId,
	                              @RequestBody(required = false) Map<String, Long> body) {
		// 支持两种方式：URL参数或请求体
		Long id = commentId != null ? commentId : (body != null ? body.get("commentId") : null);
		if (id == null) {
			throw new BizException("缺少必须的[Long]类型的参数[commentId]");
		}
		return R.success(feedCommentService.delComment(ContextUtil.getUid(), id));
	}

	@PostMapping("comment/list")
	@Operation(summary = "分页查询评论列表")
	public R<CursorPageBaseResp<FeedCommentVo>> getCommentPage(@Valid @RequestBody FeedCommentPageReq req) {
		CursorPageBaseReq pageReq = new CursorPageBaseReq();
		pageReq.setCursor(req.getCursor());
		pageReq.setPageSize(req.getPageSize());
		return R.success(feedCommentService.getCommentPage(req.getFeedId(), pageReq));
	}

	@GetMapping("comment/count")
	@Operation(summary = "获取评论数量")
	public R<Integer> getCommentCount(@RequestParam Long feedId) {
		return R.success(feedCommentService.getCommentCount(feedId));
	}

	@GetMapping("comment/all")
	@Operation(summary = "获取所有评论列表（不分页）")
	public R<List<FeedCommentVo>> getAllComments(@RequestParam Long feedId) {
		return R.success(feedCommentService.getCommentList(feedId, null));
	}

	// ==================== 点赞相关接口 ====================
	@PostMapping("like/toggle")
	@Operation(summary = "点赞或取消点赞")
	public R<Boolean> toggleLike(@Valid @RequestBody FeedLikeReq req) {
		return R.success(feedLikeService.setLike(ContextUtil.getUid(), req.getFeedId(), req.getActType()));
	}

	@GetMapping("like/list")
	@Operation(summary = "获取点赞用户列表")
	public R<List<FeedLikeVo>> getLikeList(@RequestParam Long feedId) {
		return R.success(feedLikeService.getLikeList(feedId));
	}

	@GetMapping("like/count")
	@Operation(summary = "获取点赞数量")
	public R<Integer> getLikeCount(@RequestParam Long feedId) {
		return R.success(feedLikeService.getLikeCount(feedId));
	}

	@GetMapping("like/hasLiked")
	@Operation(summary = "判断是否已点赞")
	public R<Boolean> hasLiked(@RequestParam Long feedId) {
		return R.success(feedLikeService.hasLiked(ContextUtil.getUid(), feedId));
	}
}
