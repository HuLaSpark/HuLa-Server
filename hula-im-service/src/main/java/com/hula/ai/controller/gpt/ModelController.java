package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.ModelCommand;
import com.hula.ai.gpt.pojo.param.ModelParam;
import com.hula.ai.gpt.pojo.vo.ModelVO;
import com.hula.ai.gpt.service.IModelService;
import com.hula.domain.vo.res.ApiResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  大模型信息接口
 *
 * @author: 云裂痕
 * @date: 2023-12-01
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/gpt/model")
public class ModelController {
    @Resource
    private IModelService modelService;

	/**
	 * 获取用户模型接口
	 */
	@GetMapping("/userModel")
	public ApiResult<List<ModelVO>> getUserModel(ModelParam param) {
		param.setStatus(1);
		return ApiResult.success(modelService.listModel(param));
	}

    /**
     * 查询大模型信息分页列表
     *
     * @author: 云裂痕
     * @date: 2023-12-01
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<ModelVO>> pageModel(ModelParam param) {
        return ApiResult.success(modelService.pageModel(param));
    }

    /**
     * 查询大模型信息列表
     *
     * @author: 云裂痕
     * @date: 2023-12-01
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<ModelVO>> listModel(@RequestBody ModelParam params) {
        return ApiResult.success(modelService.listModel(params));
    }

    /**
     * 获取大模型信息详细信息
     *
     * @author: 云裂痕
     * @date: 2023-12-01
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<ModelVO> getModelById(@PathVariable("id") Long id) {
        return ApiResult.success(modelService.getModelById(id));
    }

    /**
     * 新增大模型信息
     *
     * @author: 云裂痕
     * @date: 2023-12-01
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveModel(@Validated @RequestBody ModelCommand command) {
        return ApiResult.returnResult("新增", modelService.saveModel(command));
    }

    /**
     * 修改大模型信息
     *
     * @author: 云裂痕
     * @date: 2023-12-01
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateModel(@Validated @RequestBody ModelCommand command) {
		return ApiResult.returnResult("修改", modelService.updateModel(command));
    }

    /**
     * 批量删除大模型信息
     *
     * @author: 云裂痕 false
     * @date: 2023-12-01
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeModelByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", modelService.removeModelByIds(ids));
    }

}
