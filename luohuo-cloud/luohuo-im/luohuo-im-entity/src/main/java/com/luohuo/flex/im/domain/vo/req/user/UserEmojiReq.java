package com.luohuo.flex.im.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import com.luohuo.basic.base.entity.BaseEntity;
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
public class UserEmojiReq extends BaseEntity {
    /**
     * 表情地址
     */
	@Schema(description = "新增的表情url")
    private String expressionUrl;

}
