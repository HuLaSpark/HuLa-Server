package com.hula.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户背包表
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_backpack")
public class UserBackpack implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录的唯一标识ID，自增")
    private Long id;

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

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "记录创建的时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    @Schema(description = "记录最后更新的时间")
    private Date updateTime;


}
