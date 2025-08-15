package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户联系人表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user_friend")
public class UserFriend extends Entity<Long> {

    private static final long serialVersionUID = 1L;

	/**
	 * 房间id  [根据房间id+我自己的id锁定会话]
	 */
	private Long roomId;

    /**
     * uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 好友uid
     */
    @TableField("friend_uid")
    private Long friendUid;

	/**
	 * 好友备注
	 */
	private String remark;

	/**
	 * 不让他看我（0-允许，1-禁止）
	 */
	@TableField("hide_my_posts")
	private Boolean hideMyPosts;

	/**
	 * 不看他（0-允许，1-禁止）
	 */
	@TableField("hide_their_posts")
	private Boolean hideTheirPosts;

	@Schema(description = "是否临时会话")
	private Boolean isTemp;

	@Schema(description = "false-未回复 true-已回复")
	private Boolean tempStatus;

	@Schema(description = "临时消息计数")
	private Integer tempMsgCount;
}
