package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;

import com.luohuo.basic.base.entity.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 黑名单
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_black")
public class Black extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 拉黑目标类型 1.ip 2uid
     */
    @TableField("type")
    private Integer type;

    /**
     * 拉黑目标
     */
    @TableField("target")
    private String target;

	/**
	 * 截止时间
	 */
	@TableField("deadline")
	private LocalDateTime deadline;
}
