package com.luohuo.basic.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 拓展多租户的 BaseDO 基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantEntity<T> extends Entity<T> {

    /**
     * 多租户编号
     */
    private Long tenantId;

}
