package com.luohuo.flex.im.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录和前端连接的一些映射信息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestApprovalDto {
    /**
     * 操作人id
     */
    private Long uid;

    /**
     * 申请人id
     */
    private Long targetUid;
}
