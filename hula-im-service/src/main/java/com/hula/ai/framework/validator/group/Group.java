package com.hula.ai.framework.validator.group;

import javax.validation.GroupSequence;

/**
 * @description: 定义校验顺序，如果AddGroup组失败，则UpdateGroup组不会再校验
 * @author: 云裂痕
 * @date: 2019/8/16
 * @version: 3.0.0
 * 得其道
 * 乾乾
 */
@GroupSequence({AddGroup.class, UpdateGroup.class})
public interface Group {

}
