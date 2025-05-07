package com.hula.core.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消息标记表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_mark")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageMark implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 消息表id
     */
    @TableField("msg_id")
    private Long msgId;

    /**
     * 标记人uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 标记类型 @see com.hula.core.chat.domain.enums.MessageMarkTypeEnum
     */
    @TableField("type")
    private Integer type;

    /**
     * 消息状态 0正常 1取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;


}
