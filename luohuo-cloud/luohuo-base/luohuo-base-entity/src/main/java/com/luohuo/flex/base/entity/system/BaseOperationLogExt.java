package com.luohuo.flex.base.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.TenantEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import static com.luohuo.flex.model.constant.Condition.LIKE;

/**
 * <p>
 * 实体类
 * 操作日志
 * </p>
 *
 * @author 乾乾
 * @since 2021-11-08
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("base_operation_log_ext")
@AllArgsConstructor
public class BaseOperationLogExt extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;


    /**
     * 请求参数
     */
    @TableField(value = "params", condition = LIKE)
    private String params;

    /**
     * 返回值
     */
    @TableField(value = "result", condition = LIKE)
    private String result;

    /**
     * 异常描述
     */
    @TableField(value = "ex_detail", condition = LIKE)
    private String exDetail;

}
