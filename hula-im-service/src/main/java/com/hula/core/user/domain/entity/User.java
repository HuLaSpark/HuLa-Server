package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户id")
    private Long id;

    /**
     * 用户昵称
     */
    @TableField("name")
    @Schema(description = "用户昵称")
    private String name;

    /**
     * 用户头像
     */
    @TableField("avatar")
    @Schema(description = "用户头像")
    private String avatar;

    /**
     * 性别 1为男性，2为女性
     */
    @TableField("sex")
    @Schema(description = "性别 1为男性，2为女性")
    private Integer sex;

    /**
     * 微信openid用户标识
     */
    @TableField("open_id")
    @Schema(description = "微信openid用户标识")
    private String openId;

    /**
     * 在线状态 1在线 2离线
     */
    @TableField("active_status")
    @Schema(description = "在线状态 1在线 2离线")
    private Integer activeStatus;

    /**
     * 最后上下线时间
     */
    @TableField("last_opt_time")
    @Schema(description = "最后上下线时间")
    private Date lastOptTime;

    /**
     * ip信息
     */
    @TableField("ip_info")
    @Schema(description = "ip信息")
    private String ipInfo;

    /**
     * 佩戴的徽章id
     */
    @TableField("item_id")
    @Schema(description = "佩戴的徽章id")
    private Long itemId;

    /**
     * 使用状态 0.正常 1拉黑
     */
    @TableField("status")
    @Schema(description = "使用状态 0.正常 1拉黑")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    @Schema(description = "修改时间")
    private Date updateTime;


}
