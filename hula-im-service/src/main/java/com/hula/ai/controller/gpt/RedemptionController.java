package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.RedemptionCommand;
import com.hula.ai.gpt.pojo.param.RedemptionParam;
import com.hula.ai.gpt.pojo.vo.RedemptionVO;
import com.hula.ai.gpt.service.IRedemptionService;
import com.hula.domain.vo.res.ApiResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  兑换码接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/gpt/redemption")
public class RedemptionController {
    @Resource
    private IRedemptionService redemptionService;

    /**
     * 查询兑换码分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<RedemptionVO>> pageRedemption(@RequestParam RedemptionParam param) {
        return ApiResult.success(redemptionService.pageRedemption(param));
    }

    /**
     * 查询兑换码列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<RedemptionVO>> listRedemption(@RequestBody RedemptionParam param) {
        return ApiResult.success(redemptionService.listRedemption(param));
    }

    /**
     * 获取兑换码详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<RedemptionVO> getRedemptionById(@PathVariable("id") Long id) {
        return ApiResult.success(redemptionService.getRedemptionById(id));
    }

    /**
     * 新增兑换码
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveRedemption(@Validated @RequestBody RedemptionCommand command) {
        return ApiResult.returnResult("保存", redemptionService.saveRedemption(command));
    }

    /**
     * 修改兑换码
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateRedemption(@Validated @RequestBody RedemptionCommand command) {
        return ApiResult.returnResult("修改", redemptionService.updateRedemption(command));
    }

    /**
     * 批量删除兑换码
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeRedemptionByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", redemptionService.removeRedemptionByIds(ids));
    }

}
