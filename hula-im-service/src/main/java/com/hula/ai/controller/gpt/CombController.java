package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.CombCommand;
import com.hula.ai.gpt.pojo.param.CombParam;
import com.hula.ai.gpt.pojo.vo.CombVO;
import com.hula.ai.gpt.service.ICombService;
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
 *  会员套餐接口
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
@RestController
@RequestMapping("/gpt/comb")
public class CombController {
    @Resource
    private ICombService combService;

    /**
     * 查询会员套餐分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<CombVO>> pageComb(CombParam param) {
        return ApiResult.success(combService.pageComb(param));
    }

    /**
     * 查询会员套餐列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<CombVO>> listComb(@RequestBody CombParam param) {
        return ApiResult.success(combService.listComb(param));
    }

    /**
     * 获取会员套餐详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<CombVO> getCombById(@PathVariable("id") Long id) {
        return ApiResult.success(combService.getCombById(id));
    }

    /**
     * 新增会员套餐
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveComb(@Validated @RequestBody CombCommand command) {
        return ApiResult.returnResult("新增", combService.saveComb(command));
    }

    /**
     * 修改会员套餐
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateComb(@Validated @RequestBody CombCommand command) {
        return ApiResult.returnResult("修改", combService.updateComb(command));
    }

    /**
     * 批量删除会员套餐
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeCombByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", combService.removeCombByIds(ids));
    }

}
