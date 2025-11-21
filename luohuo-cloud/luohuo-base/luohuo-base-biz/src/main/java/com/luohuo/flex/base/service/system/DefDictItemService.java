package com.luohuo.flex.base.service.system;

import com.luohuo.basic.base.service.SuperService;
import com.luohuo.flex.base.entity.system.DefDict;

/**
 * <p>
 * 业务接口
 * 字典
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-04
 */
public interface DefDictItemService extends SuperService<Long, DefDict> {

    /**
     * 检查字典项标识是否可用
     *
     * @param key    标识
     * @param dictId 所属字典id
     * @param id     当前id
     * @return
     */
    boolean checkItemByKey(String key, Long dictId, Long id);
}
