package com.luohuo.flex.msg.strategy.domain;

import lombok.Data;

/**
 * @author 乾乾
 * @date 2022/7/11 0011 22:35
 */
@Data
public class BaseProperty {
    private Boolean debug;

    public boolean initAndValid() {
        if (this.debug == null) {
            this.debug = false;
        }
        return true;
    }
}
