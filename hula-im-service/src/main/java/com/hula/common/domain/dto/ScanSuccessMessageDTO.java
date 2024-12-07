package com.hula.common.domain.dto;

import com.hula.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
