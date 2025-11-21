package com.luohuo.flex.base.entity.application;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.base.entity.Entity;

import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 用户的默认应用
 * </p>
 *
 * @author 乾乾
 * @since 2022-03-06
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("def_user_application")
@AllArgsConstructor
public class DefUserApplication extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 所属用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 所属应用ID
     */
    @TableField(value = "application_id")
    private Long applicationId;


    @Builder
    public DefUserApplication(Long id, Long createdBy, LocalDateTime createdTime, Long updatedBy, LocalDateTime updatedTime,
                              Long userId, Long applicationId) {
        this.id = id;
        this.createBy = createdBy;
        this.createTime = createdTime;
        this.updateBy = updatedBy;
        this.updateTime = updatedTime;
        this.userId = userId;
        this.applicationId = applicationId;
    }

}
