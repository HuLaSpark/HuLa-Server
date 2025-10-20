package com.luohuo.flex.im.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 消息请求
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgReq implements Serializable {
    @Schema(description ="消息id, 传了消息id默认就是查询消息的接口")
    private List<Long> msgIds;

	@Schema(description ="开启同步消息")
	private Boolean async = false;
}
