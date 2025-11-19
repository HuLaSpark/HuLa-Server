package com.luohuo.flex.ai.controller.video;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.video.vo.AiVideoGenerateReqVO;
import com.luohuo.flex.ai.controller.video.vo.AiVideoPageReqVO;
import com.luohuo.flex.ai.controller.video.vo.AiVideoRespVO;
import com.luohuo.flex.ai.controller.video.vo.AiVideoUpdateReqVO;
import com.luohuo.flex.ai.controller.video.vo.GiteeAiVideoNotifyVO;
import com.luohuo.flex.ai.dal.video.AiVideoDO;
import com.luohuo.flex.ai.service.video.AiVideoService;
import com.luohuo.flex.ai.utils.BeanUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 视频生成 Controller
 *
 * @author 乾乾
 */
@Tag(name = "管理后台 - AI 视频生成")
@RestController
@RequestMapping("/video")
public class AiVideoController {

    @Resource
    private AiVideoService videoService;

    @GetMapping("/my-page")
    public R<PageResult<AiVideoRespVO>> getVideoPageMy(@Valid AiVideoPageReqVO pageReqVO) {
        PageResult<AiVideoDO> pageResult = videoService.getVideoPageMy(ContextUtil.getUid(), pageReqVO);
        return R.success(BeanUtils.toBean(pageResult, AiVideoRespVO.class));
    }

    @GetMapping("/get")
    @Parameter(name = "id", required = true, description = "视频编号", example = "1024")
    public R<AiVideoRespVO> getVideo(@RequestParam("id") Long id) {
        AiVideoDO video = videoService.getVideo(id);
        if (video == null || !ObjUtil.equal(video.getUserId(), ContextUtil.getUid())) {
            return R.success(null);
        }
        return R.success(BeanUtils.toBean(video, AiVideoRespVO.class));
    }

    @GetMapping("/my-list-by-ids")
    @Parameter(name = "ids", required = true, description = "视频编号数组", example = "1024,2048")
    public R<List<AiVideoRespVO>> getVideoListMyByIds(@RequestParam("ids") List<Long> ids) {
        List<AiVideoDO> videoList = videoService.getVideoList(ids);
        videoList.removeIf(item -> !ObjUtil.equal(ContextUtil.getUid(), item.getUserId()));
        return R.success(BeanUtils.toBean(videoList, AiVideoRespVO.class));
    }

    @Operation(summary = "生成视频")
    @PostMapping("/generate")
    public R<Long> generateVideo(@Valid @RequestBody AiVideoGenerateReqVO generateReqVO) {
        return R.success(videoService.generateVideo(ContextUtil.getUid(), generateReqVO));
    }

    @Operation(summary = "【Gitee AI】视频生成回调通知", description = "由 Gitee AI 平台回调")
    @PostMapping("/giteeai/notify")
    @PermitAll
    public R<Boolean> giteeAiNotify(@Valid @RequestBody GiteeAiVideoNotifyVO notify) {
        videoService.giteeAiNotify(notify);
        return R.success(true);
    }

    @DeleteMapping("/delete-my")
    @Parameter(name = "id", required = true, description = "视频编号", example = "1024")
    public R<Boolean> deleteVideoMy(@RequestParam("id") Long id) {
        videoService.deleteVideoMy(id, ContextUtil.getUid());
        return R.success(true);
    }

    // ================ 管理后台 ================
    @GetMapping("/page")
    public R<PageResult<AiVideoRespVO>> getVideoPage(@Valid AiVideoPageReqVO pageReqVO) {
        PageResult<AiVideoDO> pageResult = videoService.getVideoPage(pageReqVO);
        return R.success(BeanUtils.toBean(pageResult, AiVideoRespVO.class));
    }

    @PutMapping("/update")
    public R<Boolean> updateVideo(@Valid @RequestBody AiVideoUpdateReqVO updateReqVO) {
        videoService.updateVideo(updateReqVO);
        return R.success(true);
    }

    @DeleteMapping("/delete")
    @Parameter(name = "id", required = true, description = "编号", example = "1024")
    public R<Boolean> deleteVideo(@RequestParam("id") Long id) {
        videoService.deleteVideo(id);
        return R.success(true);
    }

}
