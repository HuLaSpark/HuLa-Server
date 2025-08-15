package com.luohuo.flex.im.controller.user;

import com.luohuo.flex.im.domain.vo.req.IdReqVO;
import com.luohuo.flex.im.domain.vo.res.IdRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.vo.req.user.UserEmojiReq;
import com.luohuo.flex.im.domain.vo.resp.user.UserEmojiResp;
import com.luohuo.flex.im.core.user.service.UserEmojiService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户表情包
 * @author nyh
 */
@RestController
@RequestMapping("/user/emoji")
@Tag(name = "用户表情包管理相关接口")
public class UserEmojiController {

    /**
     * 用户表情包 Service
     */
    @Resource
    private UserEmojiService emojiService;


    /**
     * 表情包列表
     *
     * @return 表情包列表
     * @author nyh
     **/
    @GetMapping("/list")
    @Operation(summary = "表情包列表")
    public R<List<UserEmojiResp>> getEmojisPage() {
        return R.success(emojiService.list(ContextUtil.getUid()));
    }


    /**
     * 新增表情包
     *
     * @param req 用户表情包
     * @return 表情包
     * @author nyh
     **/
    @PostMapping()
    @Operation(summary = "新增表情包")
    public R<IdRespVO> insertEmojis(@Valid @RequestBody UserEmojiReq req) {
        return emojiService.insert(req, ContextUtil.getUid());
    }

    /**
     * 删除表情包
     *
     * @return 删除结果
     * @author nyh
     **/
    @DeleteMapping()
    @Operation(summary = "删除表情包")
    public R<Void> deleteEmojis(@Valid @RequestBody IdReqVO reqVO) {
        emojiService.remove(reqVO.getId(), ContextUtil.getUid());
        return R.success();
    }
}
