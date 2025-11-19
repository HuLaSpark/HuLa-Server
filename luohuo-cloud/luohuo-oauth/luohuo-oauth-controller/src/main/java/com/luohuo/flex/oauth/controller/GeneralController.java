package com.luohuo.flex.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.R;
import com.luohuo.flex.model.vo.result.Option;
import com.luohuo.flex.oauth.service.DictService;
import com.luohuo.flex.oauth.service.ParamService;
import com.luohuo.flex.oauth.vo.param.CodeQueryVO;
import com.luohuo.flex.base.vo.result.system.DefDictItemResultVO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 通用 控制器
 *
 * @author 乾乾
 * @date 2019/07/25
 */
@Slf4j
@RestController
@Tag(name = "字典-枚举-参数-通用查询")
@RequiredArgsConstructor
public class GeneralController {

    private final DictService dictService;
    private final ParamService paramService;

    @Operation(summary = "根据枚举类名批量查询枚举值列表", description = "获取当前系统指定枚举")
    @PostMapping("/anyTenant/enums/findEnumMapByType")
    public R<Map<String, List<Option>>> findEnumMapByType(@RequestBody List<CodeQueryVO> types) {
        return R.success(dictService.findEnumMapByType(types));
    }

    @Operation(summary = "根据枚举类名查询枚举值列表", description = "获取当前系统指定枚举")
    @PostMapping("/anyTenant/enums/findEnumByType")
    public R<List<Option>> findEnumByType(@RequestBody CodeQueryVO type) {
        return R.success(dictService.findEnumByType(type));
    }

    @Operation(summary = "根据字典类型编码批量查询字典项,并排除指定项", description = "根据字典类型编码批量查询字典项")
    @PostMapping("/anyUser/dict/findDictMapByType")
    public R<Map<String, List<Option>>> findDictMapByType(@RequestBody List<CodeQueryVO> codeQueryVO) {
        Map<String, List<DefDictItemResultVO>> map = dictService.findDictMapByType(codeQueryVO.stream().map(CodeQueryVO::getType).toList());
        return R.success(dictService.mapOptionByDict(map, codeQueryVO));
    }

    @Operation(summary = "根据字典类型编码查询字典项,并排除指定项", description = "根据类型编码查询字典项")
    @PostMapping("/anyUser/dict/findDictByType")
    public R<List<Option>> findDictByType(@RequestBody CodeQueryVO codeQueryVO) {
        Map<String, List<DefDictItemResultVO>> map = dictService.findDictMapByType(Collections.singletonList(codeQueryVO.getType()));
        return R.success(dictService.mapOptionByDict(map, codeQueryVO));
    }

//    @GetMapping("/anyUser/parameter/value")
//    @Operation(summary = "根据key获取系统参数", description = "根据key获取系统参数")
//    public R<String> getValue(@RequestParam(value = "key") String key, @RequestParam(value = "defVal", required = false) String defVal) {
//        return R.success(parameterService.getValue(key, defVal));
//    }

    @PostMapping("/anyUser/parameter/findParamMapByKey")
    @Operation(summary = "根据key批量获取系统参数", description = "根据key批量获取系统参数")
    public R<Map<String, String>> findParams(@RequestBody List<String> keys) {
        return R.success(paramService.findParamMapByKey(keys));
    }
}

