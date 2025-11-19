package com.luohuo.basic.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 群异常码
 * @author 乾乾
 */
@AllArgsConstructor
@Getter
public enum GroupErrorEnum {

    GROUP_NOT_EXIST(9001, "该群不存在~"),
    NOT_ALLOWED_OPERATION(9002, "您无权操作~"),
    MANAGE_COUNT_EXCEED(9003, "群管理员数量达到上限，请先删除后再操作~"),
    USER_NOT_IN_GROUP(9004, "非法操作，用户不存在群聊中~"),
    NOT_ALLOWED_FOR_REMOVE(9005, "非法操作，你没有移除该成员的权限"),
    NOT_ALLOWED_FOR_EXIT_GROUP(9006, "非法操作，不允许退出大群聊"),
	PARAM_VALID(-9007, "参数校验失败{0}"),
    ;
    private final Integer code;
    private final String msg;
}
