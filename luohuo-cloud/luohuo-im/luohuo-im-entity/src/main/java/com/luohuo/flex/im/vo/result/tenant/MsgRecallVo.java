package com.luohuo.flex.im.vo.result.tenant;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息撤回
 * @author 乾乾
 */
@Data
@Builder
@NoArgsConstructor
public class MsgRecallVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;

	public MsgRecallVo(String content) {
		this.content = content;
	}
}
