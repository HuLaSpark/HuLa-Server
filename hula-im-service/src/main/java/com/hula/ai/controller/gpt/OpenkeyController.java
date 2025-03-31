package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.OpenkeyCommand;
import com.hula.ai.gpt.pojo.param.OpenKeyParam;
import com.hula.ai.gpt.pojo.vo.OpenkeyVO;
import com.hula.ai.gpt.service.IOpenkeyService;
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
 *  openai token接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/gpt/openkey")
public class OpenkeyController {
    @Resource
    private IOpenkeyService openkeyService;

    /**
     * 查询openai token分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<OpenkeyVO>> pageOpenkey(OpenKeyParam param) {
        return ApiResult.success(openkeyService.pageOpenkey(param));
    }

    /**
     * 查询openai token列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<OpenkeyVO>> listOpenkey(@RequestBody OpenKeyParam param) {
        return ApiResult.success(openkeyService.listOpenkey(param));
    }

    /**
     * 获取openai token详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<OpenkeyVO> getOpenkeyById(@PathVariable("id") Long id) {
        return ApiResult.success(openkeyService.getOpenkeyById(id));
    }

    /**
     * 新增openai token
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveOpenkey(@Validated @RequestBody OpenkeyCommand command) {
        return ApiResult.returnResult("新增", openkeyService.saveOpenkey(command));
    }

    /**
     * 修改openai token
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateOpenkey(@Validated @RequestBody OpenkeyCommand command) {
        return ApiResult.returnResult("编辑", openkeyService.updateOpenkey(command));
    }

    /**
     * 批量删除openai token
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeOpenkeyByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", openkeyService.removeOpenkeyByIds(ids));
    }

}
