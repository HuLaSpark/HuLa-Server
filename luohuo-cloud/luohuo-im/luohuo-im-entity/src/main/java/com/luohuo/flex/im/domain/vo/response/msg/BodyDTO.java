package com.luohuo.flex.im.domain.vo.response.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BodyDTO implements Serializable {
	@Schema(description = "用户uid")
	private String uid;
	@Schema(description = "消息id")
	private String messageId;
}