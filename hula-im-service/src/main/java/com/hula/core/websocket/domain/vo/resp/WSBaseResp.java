package com.hula.core.websocket.domain.vo.resp;

import com.hula.core.websocket.domain.enums.WSRespTypeEnum;

/**
 * @author nyh
 */
public class WSBaseResp<T> {
    /**
     * @see WSRespTypeEnum
     **/
    private Integer type;
    private T data;
}
