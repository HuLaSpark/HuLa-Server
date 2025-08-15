package com.luohuo.flex.im.domain.vo.request.contact;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Description: 创建会话
 * Date: 2025-07-14
 */
@Builder
@Data
public class ContactAddReq {

    @NotNull(message = "请选择与谁创建会话")
    private Long toUid;
}
