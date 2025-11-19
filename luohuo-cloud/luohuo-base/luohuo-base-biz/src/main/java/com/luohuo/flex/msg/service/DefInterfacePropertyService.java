package com.luohuo.flex.msg.service;

import com.luohuo.basic.base.service.SuperService;
import com.luohuo.flex.msg.entity.DefInterfaceProperty;
import com.luohuo.flex.msg.vo.save.DefInterfacePropertyBatchSaveVO;

import java.util.Map;


/**
 * <p>
 * 业务接口
 * 接口属性
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-04 15:51:37
 * @create [2022-07-04 15:51:37] [zuihou] [代码生成器生成]
 */
public interface DefInterfacePropertyService extends SuperService<Long, DefInterfaceProperty> {
    /**
     * 根据接口ID查询接口属性参数
     *
     * @param id
     * @return
     */
    Map<String, Object> listByInterfaceId(Long id);

    /**
     * 批量保存
     *
     * @param saveVO
     * @return
     */
    Boolean batchSave(DefInterfacePropertyBatchSaveVO saveVO);
}


