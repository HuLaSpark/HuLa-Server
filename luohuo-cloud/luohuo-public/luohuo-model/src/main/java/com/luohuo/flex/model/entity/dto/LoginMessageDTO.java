package com.luohuo.flex.model.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luohuo.basic.base.entity.BaseEntity;

import java.io.Serial;

/**
 * 将扫码登录返回信息推送给所有横向扩展的服务
 * @author nyh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMessageDTO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long uid;
    private Integer code;

}
