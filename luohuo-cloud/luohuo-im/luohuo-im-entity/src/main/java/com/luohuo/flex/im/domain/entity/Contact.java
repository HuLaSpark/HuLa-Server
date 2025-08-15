package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 会话列表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_contact")
public class Contact extends Entity<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("uid")
    private Long uid;

	@Schema(description = "房间id")
    @TableField("room_id")
    private Long roomId;

	@Schema(description = "阅读到的时间")
    @TableField("read_time")
    private LocalDateTime readTime;

	@Schema(description = "置顶会话; 都置顶按照时间倒序")
	@TableField("top")
	private Boolean top;

	@Schema(description = "通知类型 0 -> 允许接受消息 1 -> 接收但不提醒[免打扰]")
	@TableField("mute_notification")
	private Integer muteNotification;

	@Schema(description = "删除会话")
	@TableField("hide")
	private Boolean hide;

	@Schema(description = "true -> 屏蔽 false -> 正常")
	private Boolean shield;

    /**
     * 会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)
     */
    @TableField("active_time")
    private LocalDateTime activeTime;

    /**
     * 最后一条消息id
     */
    @TableField("last_msg_id")
    private Long lastMsgId;
}
