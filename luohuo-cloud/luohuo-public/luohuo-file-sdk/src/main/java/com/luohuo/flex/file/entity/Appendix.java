package com.luohuo.flex.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.base.entity.SuperEntity;

import java.time.LocalDateTime;

import static com.luohuo.flex.model.constant.Condition.LIKE;


/**
 * <p>
 * 实体类
 * 业务附件
 * </p>
 *
 * @author tangyh
 * @date 2021-06-30
 * @create [2021-06-30] [tangyh] [初始创建]
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("base_com_appendix")
@AllArgsConstructor
public class Appendix extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;
    /**
     * 业务id
     */
    @TableField(value = "biz_id")
    private Long bizId;

    /**
     * 业务类型
     */
    @TableField(value = "biz_type", condition = LIKE)
    private String bizType;


    @Builder
    public Appendix(Long id, LocalDateTime createdTime, Long createdBy, Long bizId, String bizType) {
        this.id = id;
        this.createTime = createdTime;
        this.createBy = createdBy;
        this.bizId = bizId;
        this.bizType = bizType;
    }

}
