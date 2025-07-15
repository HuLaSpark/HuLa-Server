package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户联系人表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_friend")
public class UserFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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

    /**
     * 逻辑删除(0-正常,1-删除)
     */
    @TableField("delete_status")
    private Integer deleteStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
