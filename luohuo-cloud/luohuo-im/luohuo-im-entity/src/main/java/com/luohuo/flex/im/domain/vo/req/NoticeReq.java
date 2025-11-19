package com.luohuo.flex.im.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询通知列表")
public class NoticeReq extends PageBaseReq{

	@Schema(description = "通知类型", required = true)
	private String applyType = "friend";
}