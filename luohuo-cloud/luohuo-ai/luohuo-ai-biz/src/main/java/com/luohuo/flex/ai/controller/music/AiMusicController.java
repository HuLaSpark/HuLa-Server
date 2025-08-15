package com.luohuo.flex.ai.controller.music;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.music.vo.AiMusicPageReqVO;
import com.luohuo.flex.ai.controller.music.vo.AiMusicRespVO;
import com.luohuo.flex.ai.controller.music.vo.AiMusicUpdateMyReqVO;
import com.luohuo.flex.ai.controller.music.vo.AiMusicUpdateReqVO;
import com.luohuo.flex.ai.controller.music.vo.AiSunoGenerateReqVO;
import com.luohuo.flex.ai.dal.music.AiMusicDO;
import com.luohuo.flex.ai.service.music.AiMusicService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.luohuo.basic.base.R.success;

@Tag(name = "管理后台 - AI 音乐")
@RestController
@RequestMapping("/music")
public class AiMusicController {

    @Resource
    private AiMusicService musicService;

    @GetMapping("/my-page")
    @Operation(summary = "获得【我的】音乐分页")
    public R<PageResult<AiMusicRespVO>> getMusicMyPage(@Valid AiMusicPageReqVO pageReqVO) {
        PageResult<AiMusicDO> pageResult = musicService.getMusicMyPage(pageReqVO, ContextUtil.getUid());
        return success(BeanUtils.toBean(pageResult, AiMusicRespVO.class));
    }

    @PostMapping("/generate")
    @Operation(summary = "音乐生成")
    public R<List<Long>> generateMusic(@RequestBody @Valid AiSunoGenerateReqVO reqVO) {
        return success(musicService.generateMusic(ContextUtil.getUid(), reqVO));
    }

    @Operation(summary = "删除【我的】音乐记录")
    @DeleteMapping("/delete-my")
    @Parameter(name = "id", required = true, description = "音乐编号", example = "1024")
    public R<Boolean> deleteMusicMy(@RequestParam("id") Long id) {
        musicService.deleteMusicMy(id, ContextUtil.getUid());
        return success(true);
    }

    @GetMapping("/get-my")
    @Operation(summary = "获取【我的】音乐")
    @Parameter(name = "id", required = true, description = "音乐编号", example = "1024")
    public R<AiMusicRespVO> getMusicMy(@RequestParam("id") Long id) {
        AiMusicDO music = musicService.getMusic(id);
        if (music == null || ObjUtil.notEqual(ContextUtil.getUid(), music.getUserId())) {
            return success(null);
        }
        return success(BeanUtils.toBean(music, AiMusicRespVO.class));
    }

    @PostMapping("/update-my")
    @Operation(summary = "修改【我的】音乐 目前只支持修改标题")
    @Parameter(name = "title", required = true, description = "音乐名称", example = "夜空中最亮的星")
    public R<Boolean> updateMy(AiMusicUpdateMyReqVO updateReqVO) {
        musicService.updateMyMusic(updateReqVO, ContextUtil.getUid());
        return success(true);
    }

    // ================ 音乐管理 ================

    @GetMapping("/page")
    @Operation(summary = "获得音乐分页")
    public R<PageResult<AiMusicRespVO>> getMusicPage(@Valid AiMusicPageReqVO pageReqVO) {
        PageResult<AiMusicDO> pageResult = musicService.getMusicPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiMusicRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除音乐")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteMusic(@RequestParam("id") Long id) {
        musicService.deleteMusic(id);
        return success(true);
    }

    @PutMapping("/update")
    @Operation(summary = "更新音乐")
    public R<Boolean> updateMusic(@Valid @RequestBody AiMusicUpdateReqVO updateReqVO) {
        musicService.updateMusic(updateReqVO);
        return success(true);
    }

}
