package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 群成员表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_group_member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMember extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 群组id
     */
    private Long groupId;

    /**
     * 成员uid
     */
    private Long uid;

    /**
     * 成员角色1群主(可撤回，可移除，可解散) 2管理员(可撤回，可移除) 3普通成员
     */
	private Integer roleId;

	/**
	 * 群备注
	 */
	private String remark;

	/**
	 * 我的群名称
	 */
	private String myName;

	/**
	 * 屏蔽群 1 -> 屏蔽 0 -> 正常
	 */
	private Boolean deFriend;
}
