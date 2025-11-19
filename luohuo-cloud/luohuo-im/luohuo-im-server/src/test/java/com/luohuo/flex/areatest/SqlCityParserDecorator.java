package com.luohuo.flex.areatest;


import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Component;
import com.luohuo.flex.base.entity.system.DefArea;
import com.luohuo.flex.base.manager.system.DefAreaManager;

import java.util.List;

/**
 * sql打印装饰器
 */
@Component
public class SqlCityParserDecorator {

    private DefAreaManager defAreaManager;

    public SqlCityParserDecorator(DefAreaManager defAreaManager) {
        this.defAreaManager = defAreaManager;
    }

    public List<DefArea> batchSave(List<DefArea> areaTreeList) {
        deepSave(areaTreeList);

        return areaTreeList;
    }

    /**
     * *实体转sql数据
     *
     * @param areas 省市县数据
     */
    private void deepSave(List<DefArea> areas) {
        if (CollUtil.isNotEmpty(areas)) {
            defAreaManager.saveBatch(areas);

            for (DefArea area : areas) {
                deepSave(area.getChildren());
            }
        }
    }

}
