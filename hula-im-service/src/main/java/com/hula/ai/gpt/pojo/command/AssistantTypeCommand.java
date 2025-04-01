package com.hula.ai.gpt.pojo.command;

import com.hula.ai.common.api.CommonCommand;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  助手分类对象 Command
 *
 * @author: 云裂痕
 * @date: 2023-11-22
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class AssistantTypeCommand extends CommonCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * icon图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0 禁用 1 启用
     */
    private Integer status;

}
