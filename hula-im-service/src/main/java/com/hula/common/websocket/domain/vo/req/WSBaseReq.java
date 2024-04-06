package com.hula.common.websocket.domain.vo.req;

import com.hula.common.websocket.domain.enums.WSReqTypeEnum;
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
