package com.luohuo.flex.ai.controller.audio;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.audio.vo.AiAudioGenerateReqVO;
import com.luohuo.flex.ai.controller.audio.vo.AiAudioPageReqVO;
import com.luohuo.flex.ai.controller.audio.vo.AiAudioRespVO;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowAudioApi;
import com.luohuo.flex.ai.dal.audio.AiAudioDO;
import com.luohuo.flex.ai.service.audio.AiAudioService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.luohuo.basic.base.R.success;

@Tag(name = "管理后台 - AI 音频")
@RestController
@RequestMapping("/audio")
public class AiAudioController {

    @Resource
    private AiAudioService audioService;

    @PostMapping("/generate")
    @Operation(summary = "生成音频")
    public R<Long> generateAudio(@Valid @RequestBody AiAudioGenerateReqVO generateReqVO) {
        return success(audioService.generateAudio(ContextUtil.getUid(), generateReqVO));
    }

    @GetMapping("/my-list-by-ids")
    @Operation(summary = "获取【我的】音频记录列表")
    @Parameter(name = "ids", description = "音频编号列表", required = true, example = "1024,2048")
    public R<List<AiAudioRespVO>> getAudioListMyByIds(@RequestParam("ids") List<Long> ids) {
        List<AiAudioDO> audioList = audioService.getAudioList(ids);
        audioList.removeIf(item -> !ObjUtil.equal(ContextUtil.getUid(), item.getUserId()));
        return success(BeanUtils.toBean(audioList, AiAudioRespVO.class));
    }

    @GetMapping("/get-my")
    @Operation(summary = "获取【我的】音频记录")
    @Parameter(name = "id", description = "音频编号", required = true, example = "1024")
    public R<AiAudioRespVO> getAudioMy(@RequestParam("id") Long id) {
        AiAudioDO audio = audioService.getAudio(id);
        if (audio == null || !ObjUtil.equal(ContextUtil.getUid(), audio.getUserId())) {
            return success(null);
        }
        return success(BeanUtils.toBean(audio, AiAudioRespVO.class));
    }

    @GetMapping("/my-page")
    @Operation(summary = "获取【我的】音频分页")
    public R<PageResult<AiAudioRespVO>> getAudioPageMy(@Valid AiAudioPageReqVO pageReqVO) {
        PageResult<AiAudioDO> pageResult = audioService.getAudioPageMy(ContextUtil.getUid(), pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiAudioRespVO.class));
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除【我的】音频记录")
    @Parameter(name = "id", description = "音频编号", required = true, example = "1024")
    public R<Boolean> deleteAudioMy(@RequestParam("id") Long id) {
        audioService.deleteAudioMy(id, ContextUtil.getUid());
        return success(true);
    }

    @GetMapping("/voices")
    @Operation(summary = "获取指定模型支持的声音列表")
    @Parameter(name = "model", description = "模型名称", required = true, example = "fnlp/MOSS-TTSD-v0.5")
    public R<List<String>> getSupportedVoices(@RequestParam("model") String model) {
        Set<String> voices = SiliconFlowAudioApi.getSupportedVoices(model);
        if (voices == null) {
            // 返回空列表表示支持动态音色,不限制
            return success(new ArrayList<>());
        }
        return success(new ArrayList<>(voices));
    }
}
