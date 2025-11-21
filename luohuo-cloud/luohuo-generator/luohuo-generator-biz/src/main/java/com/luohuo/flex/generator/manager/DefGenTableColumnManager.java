package com.luohuo.flex.generator.manager;

import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.flex.generator.entity.DefGenTableColumn;

import java.util.Collection;

/**
 * <p>
 * 通用业务接口
 * 代码生成字段
 * </p>
 *
 * @author 乾乾
 * @date 2022-03-01
 */
public interface DefGenTableColumnManager extends SuperManager<DefGenTableColumn> {
    /**
     * 根据 生成表ID 删除字段
     *
     * @param idList idList
     * @return boolean
     * @author tangyh
     * @date 2022/10/28 4:53 PM
     * @create [2022/10/28 4:53 PM ] [tangyh] [初始创建]
     */
    boolean removeByTableIds(Collection<Long> idList);
}
