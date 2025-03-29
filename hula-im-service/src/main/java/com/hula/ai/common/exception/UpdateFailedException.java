package com.hula.ai.common.exception;

import com.hula.ai.common.enums.ResponseEnum;
import lombok.Data;

/**
 * 数据库更新失败手动抛出异常
 *
 * @author: 云裂痕
 * @date: 2020/3/4
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class UpdateFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;

    public UpdateFailedException() {
        this.code = ResponseEnum.ERROR.getCode();
        this.msg = "数据更新失败，请稍后重新尝试";
    }

    public UpdateFailedException(String msg) {
        super(msg);
        this.code = ResponseEnum.ERROR.getCode();
        this.msg = msg;
    }

}
