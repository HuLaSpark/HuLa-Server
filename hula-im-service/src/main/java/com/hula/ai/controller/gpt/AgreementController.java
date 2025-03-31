package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.AgreementCommand;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.vo.AgreementVO;
import com.hula.ai.gpt.service.IAgreementService;
import com.hula.domain.vo.res.ApiResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  内容管理接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/gpt/content")
public class AgreementController {
    @Resource
    private IAgreementService contentService;

    /**
     * 查询内容管理分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<AgreementVO>> pageContent(@RequestParam AgreementParam param) {
        return ApiResult.success(contentService.pageContent(param));
    }

    /**
     * 查询内容管理列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<AgreementVO>> listContent(@RequestBody AgreementParam param) {
        return ApiResult.success(contentService.listContent(param));
    }

    /**
     * 获取内容管理详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<AgreementVO> getContentById(@PathVariable("id") Long id) {
        return ApiResult.success(contentService.getContentById(id));
    }

    /**
     * 新增内容管理
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveContent(@Validated @RequestBody AgreementCommand command) {
		contentService.saveContent(command);
        return ApiResult.success();
    }

    /**
     * 修改内容管理
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateContent(@Validated @RequestBody AgreementCommand command) {
        return ApiResult.returnResult("修改", contentService.updateContent(command));
    }

    /**
     * 批量删除内容管理
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeContentByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", contentService.removeContentByIds(ids));
    }

}
