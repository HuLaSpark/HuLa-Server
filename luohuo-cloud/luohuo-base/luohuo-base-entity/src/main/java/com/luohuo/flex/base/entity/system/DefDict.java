package com.luohuo.flex.base.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.TenantEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.luohuo.flex.model.constant.Condition.LIKE;

/**
 * <p>
 * 实体类
 * 字典
 * </p>
 *
 * @author 乾乾
 * @since 2021-10-04
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("def_dict")
@AllArgsConstructor
public class DefDict extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 字典ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 父字典标识
     */
    @TableField(value = "parent_key", condition = LIKE)
    private String parentKey;

    /**
     * 分类;[10-系统字典 20-业务字典]@Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.System.DICT_CLASSIFY)
     */
    @TableField(value = "classify", condition = LIKE)
    private String classify;

    /**
     * 标识
     */
    @TableField(value = "key_", condition = LIKE)
    private String key;

    /**
     * 名称
     */
    @TableField(value = "name", condition = LIKE)
    private String name;

    /**
     * 状态
     */
    @TableField(value = "state")
    private Boolean state;

    /**
     * 备注
     */
    @TableField(value = "remark", condition = LIKE)
    private String remark;

    /**
     * 排序
     */
    @TableField(value = "sort_value")
    private Integer sortValue;

    /**
     * 图标
     */
    @TableField(value = "icon", condition = LIKE)
    private String icon;

    /**
     * css样式
     */
    @TableField(value = "css_style", condition = LIKE)
    private String cssStyle;

    /**
     * css类元素
     */
    @TableField(value = "css_class", condition = LIKE)
    private String cssClass;


    @Builder
    public DefDict(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime,
                   Long parentId, String parentKey, String classify, String key, String name,
                   Boolean state, String remark, Integer sortValue, String icon, String cssStyle, String cssClass) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.parentId = parentId;
        this.parentKey = parentKey;
        this.classify = classify;
        this.key = key;
        this.name = name;
        this.state = state;
        this.remark = remark;
        this.sortValue = sortValue;
        this.icon = icon;
        this.cssStyle = cssStyle;
        this.cssClass = cssClass;
    }

}
