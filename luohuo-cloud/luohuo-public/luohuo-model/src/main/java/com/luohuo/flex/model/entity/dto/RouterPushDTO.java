package com.luohuo.flex.model.entity.dto;

import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.dto.tenant.TenantDTO;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 路由消息实体
 * @author 乾乾
 */
@Data
@NoArgsConstructor
public class RouterPushDTO extends TenantDTO {
    /**
     * 推送的ws消息
     */
    private WsBaseResp<?> wsBaseMsg;
    /**
     * 推送的uid
     */
    private List<Long> uidList;

    /**
     * 操作人uid
     */
    private Long uid;

	/**
	 * 单个用户推送
	 * @param uid 推送uid
	 * @param cuid 操作人
	 */
	public RouterPushDTO(WsBaseResp<?> wsBaseMsg, Long uid, @Nonnull Long cuid, Long tenantId) {
		super.setTenantId(tenantId);
		this.uidList = Collections.singletonList(uid);
		this.wsBaseMsg = wsBaseMsg;
		this.uid = cuid;
	}
}
