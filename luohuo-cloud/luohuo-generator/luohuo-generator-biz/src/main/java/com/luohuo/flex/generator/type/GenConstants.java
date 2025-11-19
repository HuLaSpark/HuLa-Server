package com.luohuo.flex.generator.type;

import com.luohuo.basic.base.entity.TreeEntity;

/**
 * @author 乾乾
 * @date 2022/3/13 21:46
 */
public class GenConstants {
    /**
     * 页面不需要编辑字段
     */
    public static final String[] NOT_EDIT = {TreeEntity.CREATE_BY_FIELD, TreeEntity.CREATE_TIME_FIELD,
            TreeEntity.UPDATE_BY_FIELD, TreeEntity.UPDATE_TIME_FIELD, TreeEntity.CREATE_ORG_ID_FIELD};

    /**
     * 页面不需要显示的列表字段
     */
    public static final String[] NOT_LIST = {
            TreeEntity.CREATE_BY_FIELD, TreeEntity.CREATE_TIME_FIELD,
            TreeEntity.UPDATE_BY_FIELD, TreeEntity.UPDATE_TIME_FIELD, TreeEntity.CREATE_ORG_ID_FIELD
    };

    /**
     * 页面不需要查询字段
     */
    public static final String[] NOT_QUERY = {TreeEntity.ID_FIELD, TreeEntity.CREATE_BY_FIELD, TreeEntity.CREATE_TIME_FIELD,
            TreeEntity.UPDATE_BY_FIELD, TreeEntity.UPDATE_TIME_FIELD, TreeEntity.CREATE_ORG_ID_FIELD};

}
