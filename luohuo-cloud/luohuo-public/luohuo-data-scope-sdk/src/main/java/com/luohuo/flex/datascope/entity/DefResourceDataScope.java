package com.luohuo.flex.datascope.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.base.entity.Entity;

import java.time.LocalDateTime;

import static com.luohuo.flex.model.constant.Condition.LIKE;

/**
 * <p>
 * 实体类
 * 数据权限
 * </p>
 *
 * @author 乾乾
 * @since 2022-01-10
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
public class DefResourceDataScope extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 权限名称
     */
    @TableField(value = "name", condition = LIKE)
    private String name;

    @TableField(value = "data_scope", condition = LIKE)
    private String dataScope;

    /**
     * 优先级;值越小，越优先
     */
    @TableField(value = "sort_value")
    private Integer sortValue;

    /**
     * 所属资源
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 实现类;自定义实现类全类名
     */
    @TableField(value = "custom_class", condition = LIKE)
    private String customClass;

    /**
     * 是否默认
     */
    @TableField(value = "is_def", condition = LIKE)
    private Boolean isDef;


    @Builder
    public DefResourceDataScope(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime,
                                String name, String dataScope, Integer sortValue, Long parentId, String customClass, Boolean isDef) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.name = name;
        this.dataScope = dataScope;
        this.sortValue = sortValue;
        this.parentId = parentId;
        this.customClass = customClass;
        this.isDef = isDef;
    }

}
