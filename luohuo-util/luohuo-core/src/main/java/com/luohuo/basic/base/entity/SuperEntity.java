package com.luohuo.basic.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 包括id、create_time、create_by字段的表继承的基础实体
 *
 * @author 乾乾
 * @date 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class SuperEntity<T> implements Serializable {
    public static final String ID_FIELD = "id";
    public static final String CREATE_TIME = "createTime";
    public static final String CREATE_TIME_FIELD = "create_time";
    public static final String CREATE_BY = "createBy";
    public static final String CREATE_BY_FIELD = "create_by";
    public static final String TENANT_ID = "tenant_id";
    public static final String CREATE_ORG_ID_FIELD = "create_org_id";
    public static final String DELETE_FIELD = "is_del";

    private static final long serialVersionUID = -4603110115461757622L;

    @TableId(value = "id", type = IdType.INPUT)
    @Schema(description = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    protected T id;

    @Schema(description = "创建时间")
    @TableField(value = CREATE_TIME_FIELD, fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    @Schema(description = "创建人ID")
    @TableField(value = CREATE_BY_FIELD, fill = FieldFill.INSERT)
    protected T createBy;

    @Schema(description = "逻辑删除")
    @TableField(value = DELETE_FIELD)
    @JsonIgnore
    @TableLogic(value = "false", delval = "true")
    protected Boolean isDel;

    /**
     * 保存和缺省验证组
     */
    public interface Save extends Default {

    }

    /**
     * 更新和缺省验证组
     */
    public interface Update extends Default {

    }
}
