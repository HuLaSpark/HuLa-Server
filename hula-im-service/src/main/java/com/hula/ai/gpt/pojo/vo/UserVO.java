package com.hula.ai.gpt.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  会员用户对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * uuid
     */
    private String uid;

    /**
     * 姓名
     */
    private String name;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String tel;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * openid
     */
    private String openid;

    /**
     * 登录ip
     */
    private String ip;

    /**
     * 是否开启上下文
     */
    private Boolean context;

    /**
     * 调用次数
     */
    private Long num;

    /**
     * 邀请人
     */
    private Long shareId;

    /**
     * 用户类型 1 微信小程序 2 公众号 3 手机号
     */
    private Integer type;

    /**
     * 状态 0 禁用 1 启用
     */
    private Integer status;

}
