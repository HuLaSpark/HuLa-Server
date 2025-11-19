package com.luohuo.flex.oauth.service;

import com.luohuo.basic.interfaces.echo.LoadService;
import com.luohuo.flex.model.vo.result.Option;
import com.luohuo.flex.oauth.vo.param.CodeQueryVO;
import com.luohuo.flex.base.vo.result.system.DefDictItemResultVO;

import java.util.List;
import java.util.Map;

/**
 * 字典查询服务
 *
 * @author 乾乾
 * @date 2021/10/7 13:27
 */
public interface DictService extends LoadService {
    /**
     * 根据字典key查询字典条目
     * <p>
     * 1. 先查询租户自己的字典项。
     * 2. 若不存在，则查询系统默认的字典项
     *
     * @param dictKeys 字典key
     * @return key： 字典key  value: item list
     */
    Map<String, List<DefDictItemResultVO>> findDictMapByType(List<String> dictKeys);

    List<Option> findEnumByType(CodeQueryVO type);

    Map<String, List<Option>> findEnumMapByType(List<CodeQueryVO> types);

    Map<String, List<Option>> mapOptionByDict(Map<String, List<DefDictItemResultVO>> map, List<CodeQueryVO> codeQueryVO);

    List<Option> mapOptionByDict(Map<String, List<DefDictItemResultVO>> map, CodeQueryVO codeQueryVO);
}
