package com.luohuo.flex.model.entity.dto.tenant;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 租户id
 * @author 乾乾
 */
@Data
@NoArgsConstructor
public class TenantDTO implements Serializable{
    /**
     * 租户id
     */
    private Long tenantId;

    public TenantDTO(Long tenantId) {
        this.tenantId = tenantId;
    }
}
