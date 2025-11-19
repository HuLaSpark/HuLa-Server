package com.luohuo.flex.datascope.provider;

import com.luohuo.flex.datascope.model.DataFieldProperty;

import java.util.List;

/**
 * @author 乾乾
 * @date 2022/1/9 23:28
 */
public interface DataScopeProvider {
    /**
     * 查询数据字段
     *
     * @param fsp fsp
     * @return java.util.List<com.luohuo.flex.datascope.model.DataFieldProperty>
     * @author tangyh
     * @date 2022/10/28 4:41 PM
     * @create [2022/10/28 4:41 PM ] [tangyh] [初始创建]
     */
    List<DataFieldProperty> findDataFieldProperty(List<DataFieldProperty> fsp);
}
