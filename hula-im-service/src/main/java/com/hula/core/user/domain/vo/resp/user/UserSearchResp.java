package com.hula.core.user.domain.vo.resp.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 用户搜索响应
 * @author mint
 */
@Data
@Builder
public class UserSearchResp {
    /**
     * 用户id
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String account;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String name;

    /**
     * 头像URL
     */
    @Schema(description = "用户头像")
    private String avatar;
}
