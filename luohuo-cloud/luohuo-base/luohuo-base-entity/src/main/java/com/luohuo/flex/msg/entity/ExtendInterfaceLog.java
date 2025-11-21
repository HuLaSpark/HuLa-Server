package com.luohuo.flex.msg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.luohuo.flex.model.constant.Condition.LIKE;


/**
 * <p>
 * 实体类
 * 接口执行日志
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-09 23:58:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@TableName("extend_interface_log")
//@TenantIgnore
public class ExtendInterfaceLog extends Entity<Long> {
    private static final long serialVersionUID = 1L;

    /**
     * 接口ID;
     * #extend_interface
     */
    @TableField(value = "interface_id", condition = EQUAL)
    private Long interfaceId;
    /**
     * 接口名称
     */
    @TableField(value = "name", condition = LIKE)
    private String name;
    /**
     * 成功次数
     */
    @TableField(value = "success_count", condition = EQUAL)
    private Integer successCount;
    /**
     * 失败次数
     */
    @TableField(value = "fail_count", condition = EQUAL)
    private Integer failCount;
    /**
     * 最后执行时间
     */
    @TableField(value = "last_exec_time", condition = EQUAL)
    private LocalDateTime lastExecTime;


}
