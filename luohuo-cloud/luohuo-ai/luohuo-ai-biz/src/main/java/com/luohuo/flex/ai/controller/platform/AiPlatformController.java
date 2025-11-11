package com.luohuo.flex.ai.controller.platform;

import com.luohuo.basic.base.R;
import com.luohuo.flex.ai.controller.platform.vo.AiPlatformAddModelReqVO;
import com.luohuo.flex.ai.controller.platform.vo.AiPlatformRespVO;
import com.luohuo.flex.ai.dal.platform.AiPlatformDO;
import com.luohuo.flex.ai.service.platform.AiPlatformService;
import com.luohuo.flex.ai.utils.BeanUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.luohuo.basic.base.R.success;

/**
 * AI 平台配置 Controller
 *
 * @author 乾乾
 */
@Tag(name = "管理后台 - AI 平台配置")
@RestController
@RequestMapping("/platform")
@Validated
public class AiPlatformController {

    @Resource
    private AiPlatformService platformService;

    @GetMapping("/list")
    @Operation(summary = "获得平台列表")
    public R<List<AiPlatformRespVO>> getPlatformList() {
        List<AiPlatformDO> list = platformService.getPlatformList();
        return success(BeanUtils.toBean(list, AiPlatformRespVO.class));
    }

    @PostMapping("/add-model")
    @Operation(summary = "添加平台模型到示例列表")
    public R<Boolean> addModelToExamples(@Valid @RequestBody AiPlatformAddModelReqVO reqVO) {
        platformService.addModelToExamples(reqVO.getPlatform(), reqVO.getModel());
        return success(true);
    }

    @PostMapping("/clean-duplicate-models")
    @Operation(summary = "清理所有平台的重复模型")
    public R<Integer> cleanDuplicateModels() {
        int cleanedCount = platformService.cleanDuplicateModels();
        return success(cleanedCount);
    }

}