package com.luohuo.flex.oauth.controller;


import com.luohuo.basic.tenant.core.aop.TenantIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.R;
import com.luohuo.basic.model.log.OptLogDTO;
import com.luohuo.basic.utils.BeanPlusUtil;
import com.luohuo.flex.base.service.system.BaseOperationLogService;
import com.luohuo.flex.base.vo.save.system.BaseOperationLogSaveVO;

/**
 * <p>
 * 前端控制器
 * 系统日志
 * </p>
 *
 * @author 乾乾
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping
@Tag(name = "系统访问日志")
@AllArgsConstructor
@Hidden
public class WebLogController {

    private final BaseOperationLogService baseOperationLogService;

    /**
     * 保存系统日志
     *
     * @param data 保存对象
     * @return 保存结果
     */
    @PostMapping("/optLog")
    @Operation(summary = "保存系统日志", description = "保存系统日志不为空的字段")
    @TenantIgnore
    public R<Boolean> save(@RequestBody OptLogDTO data) {
        baseOperationLogService.save(BeanPlusUtil.toBean(data, BaseOperationLogSaveVO.class));
        return R.success(true);
    }

}
