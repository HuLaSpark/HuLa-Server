package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 功能物品配置表
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_item_config")
@Schema(description = "功能物品配置表")
public class ItemConfig extends Entity<Long> {

    private static final long serialVersionUID = 1L;

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
     * 注意：字段名为"describe"可能与Java关键字冲突，建议修改为"itemDescription"或其他非关键字名称
     */
    @TableField("`describe`")
    @Schema(description = "对物品功能的详细描述")
    private String describe;
}
