package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能物品配置表
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("item_config")
@Schema(description = "功能物品配置表")
public class ItemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId("id")
    @Schema(description = "唯一标识ID")
    private Long id;

    /**
     * 物品类型 1改名卡 2徽章
     */
    @TableField("type")
    @Schema(description = "物品类型：1表示改名卡，2表示徽章")
    private Integer type;

    /**
     * 物品图片
     */
    @TableField("img")
    @Schema(description = "物品的图片链接")
    private String img;

    /**
     * 物品功能描述
     */
    @TableField("describe")
    @Schema(description = "对物品功能的详细描述")
    private String describe; // 注意：字段名为"describe"可能与Java关键字冲突，建议修改为"itemDescription"或其他非关键字名称

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
