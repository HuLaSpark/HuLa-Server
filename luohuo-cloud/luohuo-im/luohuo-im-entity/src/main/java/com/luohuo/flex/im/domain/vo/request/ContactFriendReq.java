package com.luohuo.flex.im.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * 查询好友会话专用
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactFriendReq {

    @NotNull(message = "请选择会话")
    @Schema(description ="单聊为好友uid 群聊为房间id")
    private Long id;

	@NotNull(message = "请选择房间类型")
    private Integer roomType;
}
