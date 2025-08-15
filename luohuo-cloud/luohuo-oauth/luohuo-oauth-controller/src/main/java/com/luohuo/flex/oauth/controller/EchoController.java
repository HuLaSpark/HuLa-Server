package com.luohuo.flex.oauth.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.annotation.response.IgnoreResponseBodyAdvice;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseOrgService;
import com.luohuo.flex.base.service.user.BasePositionService;
import com.luohuo.flex.oauth.service.DictService;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 数据注入的查询实现类
 *
 * @author zuihou
 * @date 2020/9/25 9:15 上午
 */
@Slf4j
@RestController
@AllArgsConstructor()
@RequestMapping("/echo")
@IgnoreResponseBodyAdvice
@Tag(name = "数据注入查询接口， 不建议前端调用")
@Hidden
public class EchoController {
    private final DictService dictService;
    private final BaseOrgService baseOrgService;
    private final DefUserService defUserService;
    private final BasePositionService basePositionService;

    @Operation(summary = "根据id查询用户", description = "根据id查询用户")
    @PostMapping("/user/findByIds")
    public Map<Serializable, Object> findUserByIds(@RequestParam(value = "ids") Set<Serializable> ids) {
        return defUserService.findByIds(ids);
    }

    @PostMapping("/position/findByIds")
    public Map<Serializable, Object> findStationByIds(@RequestParam("ids") Set<Serializable> ids) {
        return basePositionService.findByIds(ids);
    }

    @PostMapping("/org/findByIds")
    public Map<Serializable, Object> findOrgByIds(@RequestParam("ids") Set<Serializable> ids) {
        return baseOrgService.findByIds(ids);
    }

    @Operation(summary = "查询字典项", description = "根据字典编码查询字典项")
    @PostMapping("/dict/findByIds")
    public Map<Serializable, Object> findDictByIds(@RequestParam("ids") Set<Serializable> ids) {
        return this.dictService.findByIds(ids);
    }

}
