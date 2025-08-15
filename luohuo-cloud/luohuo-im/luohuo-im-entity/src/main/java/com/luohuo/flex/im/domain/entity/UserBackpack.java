package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 用户背包表
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user_backpack")
@Schema(description = "用户背包表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBackpack extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("uid")
    @Schema(description = "关联的用户ID")
    private Long uid;

    /**
     * 物品ID
     */
    @TableField("item_id")
    @Schema(description = "物品的唯一标识ID")
    private Long itemId;

    /**
     * 使用状态 0.待使用 1.已使用
     */
    @TableField("status")
    @Schema(description = "物品的状态：0表示待使用，1表示已使用")
    private Integer status;

    /**
     * 幂等号
     */
    @TableField("idempotent")
    @Schema(description = "用于确保操作幂等性的唯一标识")
    private String idempotent;

}
