package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.AssistantTypeCommand;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.param.AssustantTypeParams;
import com.hula.ai.gpt.pojo.vo.AssistantTypeVO;
import com.hula.ai.gpt.service.IAssistantTypeService;
import com.hula.domain.vo.res.ApiResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  助手分类接口
 *
 * @author: 云裂痕
 * @date: 2023-11-22
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/gpt/assistant-type")
public class AssistantTypeController {
    @Resource
    private IAssistantTypeService assistantTypeService;

    /**
     * 查询助手分类分页列表
     *
     * @author: 云裂痕
     * @date: 2023-11-22
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<AssistantTypeVO>> pageAssistantType(@RequestParam AssustantTypeParams param) {
        return ApiResult.success(assistantTypeService.pageAssistantType(param));
    }

    /**
     * 查询助手分类列表
     *
     * @author: 云裂痕
     * @date: 2023-11-22
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<AssistantTypeVO>> listAssistantType(@RequestBody AgreementParam param) {
        return ApiResult.success(assistantTypeService.listAssistantType(param));
    }

    /**
     * 获取助手分类详细信息
     *
     * @author: 云裂痕
     * @date: 2023-11-22
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<AssistantTypeVO> getAssistantTypeById(@PathVariable("id") Long id) {
        return ApiResult.success(assistantTypeService.getAssistantTypeById(id));
    }

    /**
     * 新增助手分类
     *
     * @author: 云裂痕
     * @date: 2023-11-22
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveAssistantType(@Validated @RequestBody AssistantTypeCommand command) {
        return ApiResult.returnResult("新增", assistantTypeService.saveAssistantType(command));
    }

    /**
     * 修改助手分类
     *
     * @author: 云裂痕
     * @date: 2023-11-22
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateAssistantType(@Validated @RequestBody AssistantTypeCommand command) {
        return ApiResult.returnResult("修改", assistantTypeService.updateAssistantType(command));
    }

    /**
     * 批量删除助手分类
     *
     * @author: 云裂痕 false
     * @date: 2023-11-22
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeAssistantTypeByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", assistantTypeService.removeAssistantTypeByIds(ids));
    }

}
