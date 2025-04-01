package com.hula.ai.gpt.pojo.command;

import com.hula.ai.common.api.CommonCommand;
import lombok.Data;

import java.io.Serializable;

/**
 *  内容管理对象 Command
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class AgreementCommand extends CommonCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 内容
     */
    private String content;

}
