package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.AssistantCommand;
import com.hula.ai.gpt.pojo.param.AssustantParams;
import com.hula.ai.gpt.pojo.vo.AssistantVO;
import com.hula.ai.gpt.service.IAssistantService;
import com.hula.domain.vo.res.ApiResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  AI助理功能接口
 *
 * @author: 云裂痕
 * @date: 2025-03-06
 * 得其道 乾乾
 */
@RestController
@RequestMapping("/gpt/assistant")
public class AssistantController {
    @Resource
    private IAssistantService assistantService;

    /**
     * 查询AI助理功能分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<AssistantVO>> pageAssistant(@RequestParam AssustantParams param) {
        return ApiResult.success(assistantService.pageAssistant(param));
    }

    /**
     * 查询AI助理功能列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<AssistantVO>> listAssistant(@RequestBody AssustantParams param) {
        return ApiResult.success(assistantService.listAssistant(param));
    }

    /**
     * 获取AI助理功能详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<AssistantVO> getAssistantById(@PathVariable("id") Long id) {
        return assistantService.getAssistantById(id);
    }

    /**
     * 新增AI助理功能
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveAssistant(@Validated @RequestBody AssistantCommand command) {
        return assistantService.saveAssistant(command);
    }

    /**
     * 修改AI助理功能
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateAssistant(@Validated @RequestBody AssistantCommand command) {
        return assistantService.updateAssistant(command);
    }

    /**
     * 批量删除AI助理功能
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeAssistantByIds(@PathVariable List<Long> ids) {
        return assistantService.removeAssistantByIds(ids);
    }

}
