package com.hula.ai.controller.write;


import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.write.vo.AiWriteGenerateReqVO;
import com.hula.ai.controller.write.vo.AiWritePageReqVO;
import com.hula.ai.controller.write.vo.AiWriteRespVO;
import com.hula.ai.dal.write.AiWriteDO;
import com.hula.ai.service.write.AiWriteService;
import com.hula.ai.utils.BeanUtils;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static com.hula.ai.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - AI 写作")
@RestController
@RequestMapping("/ai/write")
public class AiWriteController {

    @Resource
    private AiWriteService writeService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "写作生成（流式）", description = "流式返回，响应较快")
    public Flux<ApiResult<String>> generateWriteContent(@RequestBody @Valid AiWriteGenerateReqVO generateReqVO) {
        return writeService.generateWriteContent(generateReqVO, RequestHolder.get().getUid());
    }

    // ================ 写作管理 ================

    @DeleteMapping("/delete")
    @Operation(summary = "删除写作")
    @Parameter(name = "id", description = "编号", required = true)
    public ApiResult<Boolean> deleteWrite(@RequestParam("id") Long id) {
        writeService.deleteWrite(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得写作分页")
    public ApiResult<PageResult<AiWriteRespVO>> getWritePage(@Valid AiWritePageReqVO pageReqVO) {
        PageResult<AiWriteDO> pageResult = writeService.getWritePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiWriteRespVO.class));
    }

}
