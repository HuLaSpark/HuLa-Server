package com.hula.core.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会话列表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("contact")
public class Contact implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 房间id
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 阅读到的时间
     */
    @TableField("read_time")
    private Date readTime;

	/**
	 * 置顶会话; 都置顶按照时间倒序
	 */
	@TableField("top")
	private Boolean top;

	/**
	 * 通知类型 0 -> 允许接受消息 1 -> 接收但不提醒[免打扰]
	 */
	@TableField("mute_notification")
	private Integer muteNotification;

	/**
	 * 删除会话
	 */
	@TableField("hide")
	private Boolean hide;

    /**
     * 会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)
     */
    @TableField("active_time")
    private Date activeTime;

    /**
     * 最后一条消息id
     */
    @TableField("last_msg_id")
    private Long lastMsgId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

	@Schema(description = "true -> 屏蔽 false -> 正常")
	private Boolean shield;
}
