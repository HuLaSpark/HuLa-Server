package com.luohuo.basic.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 包括id、create_time、create_by、update_by、update_time、label、parent_id、sort_value 字段的表继承的树形实体
 *
 * @author 乾乾
 * @date 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class TreeEntity<E, T extends Serializable> extends Entity<T> {

    public static final String LABEL = "label";
    public static final String PARENT_ID = "parentId";
    public static final String SORT_VALUE = "sortValue";
    public static final String PARENT_ID_FIELD = "parent_id";
    public static final String SORT_VALUE_FIELD = "sort_value";

    /**
     * 父ID
     */
    @Schema(description = "父ID")
    @TableField(value = "parent_id")
    protected T parentId;

    /**
     * 排序
     */
    @Schema(description = "排序号")
    @TableField(value = "sort_value")
    protected Integer sortValue;


    @Schema(description = "子节点", hidden = true)
    @TableField(exist = false)
    protected List<E> children;


    /**
     * 初始化子类
     */
    @JsonIgnore
    public void initChildren() {
        if (getChildren() == null) {
            this.setChildren(new ArrayList<>());
        }
    }

    @JsonIgnore
    public void addChildren(E child) {
        initChildren();
        children.add(child);
    }
}
