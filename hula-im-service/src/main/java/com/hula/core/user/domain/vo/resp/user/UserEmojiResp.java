package com.hula.core.user.domain.vo.resp.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 表情包反参
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiResp {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 表情地址
     */
    @ApiModelProperty(value = "表情url")
    private String expressionUrl;

}
