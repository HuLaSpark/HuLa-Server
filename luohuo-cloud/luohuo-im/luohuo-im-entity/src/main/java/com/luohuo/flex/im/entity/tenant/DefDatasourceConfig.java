package com.luohuo.flex.im.entity.tenant;

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

import static com.luohuo.flex.model.constant.Condition.LIKE;

/**
 * <p>
 * 实体类
 * 数据源
 * </p>
 *
 * @author 乾乾
 * @since 2021-09-15
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("def_datasource_config")
@AllArgsConstructor
public class DefDatasourceConfig extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @TableField(value = "name", condition = LIKE)
    private String name;

    /**
     * 用户名
     */
    @TableField(value = "username", condition = LIKE)
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password", condition = LIKE)
    private String password;

    /**
     * 链接
     */
    @TableField(value = "url", condition = LIKE)
    private String url;

    /**
     * 驱动
     */
    @TableField(value = "driver_class_name", condition = LIKE)
    private String driverClassName;


    @Builder
    public DefDatasourceConfig(Long id, LocalDateTime createdTime, Long createdBy, LocalDateTime updatedTime, Long updatedBy,
                               String name, String username, String password, String url, String driverClassName) {
        this.id = id;
        this.createTime = createdTime;
        this.createBy = createdBy;
        this.updateTime = updatedTime;
        this.updateBy = updatedBy;
        this.name = name;
        this.username = username;
        this.password = password;
        this.url = url;
        this.driverClassName = driverClassName;
    }

}
