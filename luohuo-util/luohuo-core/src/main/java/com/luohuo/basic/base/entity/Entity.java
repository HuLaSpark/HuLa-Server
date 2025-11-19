package com.luohuo.basic.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 包括id、create_time、create_by、update_by、update_time字段的表继承的基础实体
 *
 * @author 乾乾
 * @date 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Entity<T> extends SuperEntity<T> {

    public static final String UPDATE_TIME = "updateTime";
    public static final String UPDATE_BY = "updateBy";
    public static final String UPDATE_TIME_FIELD = "update_time";
    public static final String UPDATE_BY_FIELD = "update_by";
    private static final long serialVersionUID = 5169873234271173683L;

    @Schema(description = "最后修改时间")
    @TableField(value = UPDATE_TIME_FIELD, fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;

    @Schema(description = "最后修改人ID")
    @TableField(value = UPDATE_BY_FIELD, fill = FieldFill.INSERT_UPDATE)
    protected T updateBy;
}
