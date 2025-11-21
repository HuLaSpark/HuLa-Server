package com.luohuo.flex.msg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.luohuo.flex.model.constant.Condition.LIKE;


/**
 * <p>
 * 实体类
 * 消息接收人
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-10 11:41:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@TableName("extend_msg_recipient")
//@TenantIgnore
public class ExtendMsgRecipient extends Entity<Long> {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID;
     * <p>
     * #extend_msg
     */
    @TableField(value = "msg_id", condition = EQUAL)
    private Long msgId;
    /**
     * 接收人;
     * 可能是手机号、邮箱、用户ID等
     */
    @TableField(value = "recipient", condition = LIKE)
    private String recipient;

    /**
     * 扩展信息
     */
    @TableField(value = "ext", condition = LIKE)
    private String ext;


}
