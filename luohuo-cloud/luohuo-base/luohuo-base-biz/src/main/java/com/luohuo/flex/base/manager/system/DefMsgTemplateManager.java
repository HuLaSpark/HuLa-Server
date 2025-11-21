package com.luohuo.flex.base.manager.system;

import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.flex.base.entity.system.DefMsgTemplate;

/**
 * <p>
 * 通用业务接口
 * 消息模板
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-04 15:51:37
 * @create [2022-07-04 15:51:37] [zuihou] [代码生成器生成]
 */
public interface DefMsgTemplateManager extends SuperManager<DefMsgTemplate> {
    /**
     * 根据消息模板编码查询消息模板
     *
     * @param code
     * @return
     */
    DefMsgTemplate getByCode(String code);
}


