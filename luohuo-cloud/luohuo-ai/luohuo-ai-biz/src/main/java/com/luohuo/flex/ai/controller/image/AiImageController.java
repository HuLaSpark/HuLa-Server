package com.luohuo.flex.ai.controller.image;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.image.vo.AiImageDrawReqVO;
import com.luohuo.flex.ai.controller.image.vo.AiImagePageReqVO;
import com.luohuo.flex.ai.controller.image.vo.AiImagePublicPageReqVO;
import com.luohuo.flex.ai.controller.image.vo.AiImageRespVO;
import com.luohuo.flex.ai.controller.image.vo.AiImageUpdateReqVO;
import com.luohuo.flex.ai.controller.image.vo.midjourney.AiMidjourneyActionReqVO;
import com.luohuo.flex.ai.controller.image.vo.midjourney.AiMidjourneyImagineReqVO;
import com.luohuo.flex.ai.core.model.MidjourneyApi;
import com.luohuo.flex.ai.dal.image.AiImageDO;
import com.luohuo.flex.ai.service.image.AiImageService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.luohuo.basic.base.R.success;

@Tag(name = "管理后台 - AI 绘画")
@RestController
@RequestMapping("/image")
@Slf4j
public class AiImageController {

    @Resource
    private AiImageService imageService;

    @GetMapping("/my-page")
    @Operation(summary = "获取【我的】绘图分页")
    public R<PageResult<AiImageRespVO>> getImagePageMy(@Validated AiImagePageReqVO pageReqVO) {
        PageResult<AiImageDO> pageResult = imageService.getImagePageMy(ContextUtil.getUid(), pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiImageRespVO.class));
    }

    @GetMapping("/public-page")
    @Operation(summary = "获取公开的绘图分页")
    public R<PageResult<AiImageRespVO>> getImagePagePublic(AiImagePublicPageReqVO pageReqVO) {
        PageResult<AiImageDO> pageResult = imageService.getImagePagePublic(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiImageRespVO.class));
    }

    @GetMapping("/get-my")
    @Operation(summary = "获取【我的】绘图记录")
    @Parameter(name = "id", required = true, description = "绘画编号", example = "1024")
    public R<AiImageRespVO> getImageMy(@RequestParam("id") Long id) {
        AiImageDO image = imageService.getImage(id);
        if (image == null || ObjUtil.notEqual(ContextUtil.getUid(), image.getUserId())) {
            return success(null);
        }
        return success(BeanUtils.toBean(image, AiImageRespVO.class));
    }

    @GetMapping("/my-list-by-ids")
    @Operation(summary = "获取【我的】绘图记录列表")
    @Parameter(name = "ids", required = true, description = "绘画编号数组", example = "1024,2048")
    public R<List<AiImageRespVO>> getImageListMyByIds(@RequestParam("ids") List<Long> ids) {
        List<AiImageDO> imageList = imageService.getImageList(ids);
        imageList.removeIf(item -> !ObjUtil.equal(ContextUtil.getUid(), item.getUserId()));
        return success(BeanUtils.toBean(imageList, AiImageRespVO.class));
    }

    @Operation(summary = "生成图片")
    @PostMapping("/draw")
    public R<Long> drawImage(@Valid @RequestBody AiImageDrawReqVO drawReqVO) {
        return success(imageService.drawImage(ContextUtil.getUid(), drawReqVO));
    }

    @Operation(summary = "删除【我的】绘画记录")
    @DeleteMapping("/delete-my")
    @Parameter(name = "id", required = true, description = "绘画编号", example = "1024")
    public R<Boolean> deleteImageMy(@RequestParam("id") Long id) {
        imageService.deleteImageMy(id, ContextUtil.getUid());
        return success(true);
    }

    // ================ midjourney 专属 ================

    @Operation(summary = "【Midjourney】生成图片")
    @PostMapping("/midjourney/imagine")
    public R<Long> midjourneyImagine(@Valid @RequestBody AiMidjourneyImagineReqVO reqVO) {
        Long imageId = imageService.midjourneyImagine(ContextUtil.getUid(), reqVO);
        return success(imageId);
    }

    @Operation(summary = "【Midjourney】通知图片进展", description = "由 Midjourney Proxy 回调")
    @PostMapping("/midjourney/notify") // 必须是 POST 方法，否则会报错
    @PermitAll
    public R<Boolean> midjourneyNotify(@Valid @RequestBody MidjourneyApi.Notify notify) {
        imageService.midjourneyNotify(notify);
        return success(true);
    }

    @Operation(summary = "【Midjourney】Action 操作（二次生成图片）", description = "例如说：放大、缩小、U1、U2 等")
    @PostMapping("/midjourney/action")
    public R<Long> midjourneyAction(@Valid @RequestBody AiMidjourneyActionReqVO reqVO) {
        Long imageId = imageService.midjourneyAction(ContextUtil.getUid(), reqVO);
        return success(imageId);
    }

    // ================ 绘图管理 ================

    @GetMapping("/page")
    @Operation(summary = "获得绘画分页")
    public R<PageResult<AiImageRespVO>> getImagePage(@Valid AiImagePageReqVO pageReqVO) {
        PageResult<AiImageDO> pageResult = imageService.getImagePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiImageRespVO.class));
    }

    @PutMapping("/update")
    @Operation(summary = "更新绘画")
    public R<Boolean> updateImage(@Valid @RequestBody AiImageUpdateReqVO updateReqVO) {
        imageService.updateImage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除绘画")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteImage(@RequestParam("id") Long id) {
        imageService.deleteImage(id);
        return success(true);
    }

}