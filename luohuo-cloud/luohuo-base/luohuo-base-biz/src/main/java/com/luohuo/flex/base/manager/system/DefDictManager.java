package com.luohuo.flex.base.manager.system;

import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.basic.interfaces.echo.LoadService;
import com.luohuo.flex.base.entity.system.DefDict;
import com.luohuo.flex.base.vo.result.system.DefDictItemResultVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 字典管理
 *
 * @author tangyh
 * @version v1.0
 * @date 2021/9/29 1:26 下午
 * @create [2021/9/29 1:26 下午 ] [tangyh] [初始创建]
 */
public interface DefDictManager extends SuperManager<DefDict>, LoadService {

    /**
     * 根据字典key查询系统默认的字典条目
     *
     * @param dictKeys 字典key
     * @return key： 字典key  value: item list
     */
    Map<String, List<DefDictItemResultVO>> findDictMapItemListByKey(List<String> dictKeys);

    /**
     * 删除字典条目
     *
     * @param idList idList
     * @return boolean
     * @author tangyh
     * @date 2022/4/18 1:34 PM
     * @create [2022/4/18 1:34 PM ] [tangyh] [初始创建]
     */
    boolean removeItemByIds(Collection<Long> idList);
}
