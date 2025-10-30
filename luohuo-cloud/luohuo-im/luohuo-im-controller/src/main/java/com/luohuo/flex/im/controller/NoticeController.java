package com.luohuo.flex.im.controller;

import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.core.user.service.NoticeService;
import com.luohuo.flex.im.domain.vo.req.NoticeReadReq;
import com.luohuo.flex.im.domain.vo.req.NoticeReq;
import com.luohuo.flex.im.domain.vo.res.NoticeVO;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.model.entity.ws.WSNotice;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room/notice")
public class NoticeController {
	@Resource
    private NoticeService noticeService;

    @GetMapping("/page")
    public R<PageBaseResp<NoticeVO>> getNotices(@Valid NoticeReq request) {
        Long uid = ContextUtil.getUid();
        return R.success(noticeService.getUserNotices(uid, request));
    }

    @PostMapping("/read")
    public R<Void> markAsRead(@RequestBody NoticeReadReq req) {
        noticeService.markAsRead(req.getNoticeId());
        return R.success();
    }

	@GetMapping("/unread")
	@Operation(summary = "通知未读数")
	public R<WSNotice> unread() {
		Long uid = ContextUtil.getUid();
		return R.success(noticeService.unread(uid));
	}
    
}