package com.HuLa.common.websocket.domain.vo.req;

import com.HuLa.common.websocket.domain.enums.WSReqTypeEnum;
import lombok.Data;

/**
 * @author nyh
 */
@Data
public class WSBaseReq {
    /**
     * @see WSReqTypeEnum
     **/
    private Integer type;
    private String data;
}
