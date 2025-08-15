package com.luohuo.flex.model.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luohuo.basic.base.entity.BaseEntity;

/**
 * 扫码成功对象，推送给用户的消息对象
 * @author nyh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanSuccessMessageDTO  extends BaseEntity {
    /**
     * 推送的code
     */
    private Integer code;

}
