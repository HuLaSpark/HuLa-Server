package com.luohuo.flex.im.domain.vo.resp.user;

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
public class UserEmojiResp extends BaseEntity {
	@Schema(description = "id")
    private Long id;

	@Schema(description = "表情url")
    private String expressionUrl;

}
