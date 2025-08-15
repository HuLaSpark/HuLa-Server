package com.luohuo.flex.im.domain.vo.request.room;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Description: 查询好友
 * Date: 2023-03-23
 */
@Data
public class RoomGroupReq {

    @NotEmpty(message = "请输入群号")
	private String account;
}
