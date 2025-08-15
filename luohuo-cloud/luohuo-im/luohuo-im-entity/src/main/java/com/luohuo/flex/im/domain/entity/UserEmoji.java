package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.luohuo.basic.base.entity.Entity;
import lombok.*;

/**
 * <p>
 * 用户表情包
 * </p>
 *
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("im_user_emoji")
public class UserEmoji extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户表ID
     */
    @TableField("uid")
    private Long uid;

    /**
     * 表情地址
     */
    @TableField("expression_url")
    private String expressionUrl;
}
