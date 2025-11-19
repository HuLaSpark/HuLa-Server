package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 微信消息表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_wx_msg")
public class WxMsg extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 微信openid用户标识
     */
    @TableField("open_id")
    private String openId;

    /**
     * 用户消息
     */
    @TableField("msg")
    private String msg;
}
