package com.luohuo.flex.oauth.vo.result;


import cn.hutool.core.map.MapUtil;
import com.luohuo.flex.base.vo.result.application.DefApplicationResultVO;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.annotation.echo.Echo;
import com.luohuo.basic.base.entity.Entity;
import com.luohuo.basic.interfaces.echo.EchoVO;
import com.luohuo.flex.model.constant.EchoApi;
import com.luohuo.flex.model.constant.EchoDictType;

import java.io.Serializable;
import java.util.Map;


/**
 * <p>
 * 实体类
 * 用户
 * </p>
 *
 * @author 乾乾
 * @since 2021-10-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "用户")
public class DefUserInfoResultVO extends Entity<Long> implements Serializable, EchoVO {

    private static final long serialVersionUID = 1L;
    @Builder.Default
    private Map<String, Object> echoMap = MapUtil.newHashMap();

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机")
    private String mobile;

    /**
     * 身份证;15或18位
     */
    @Schema(description = "身份证")
    private String idCard;

    /**
     * 性别;
     * #Sex{W:女;M:男;N:未知}
     */
    @Schema(description = "性别")
    @Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Global.SEX)
    private String sex;

    /**
     * 状态;[0-禁用 1-启用]
     */
    @Schema(description = "状态")
    private Boolean state;

    @Schema(description = "头像")
    private String avatar;

    /**
     * 工作描述
     */
    @Schema(description = "工作描述")
    private String workDescribe;

    @Schema(description = "员工ID")
	private Long uid;

    @Schema(description = "当前员工信息")
    private BaseEmployeeResultVO baseEmployee;

    @Schema(description = "当前应用信息")
    private DefApplicationResultVO defApplication;

    /** 为空时，默认页面由前端控制 */
    @Schema(description = "登录成功后，跳转的页面")
    private String homePath;
}
