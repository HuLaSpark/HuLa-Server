package com.luohuo.flex.im.domain.entity.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 合并消息实体
 * @author 乾乾
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MergeMsg implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	@Schema(description ="内容")
    private String content;

	@Schema(description = "发送时间")
	private LocalDateTime createdTime;

	@Schema(description ="发送人名称")
	private String name;
}
