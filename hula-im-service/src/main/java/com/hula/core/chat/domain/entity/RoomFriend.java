package com.hula.core.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 单聊房间表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room_friend")
public class RoomFriend implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

	@Schema(description = "房间id")
    @TableField("room_id")
    private Long roomId;

    @TableField("uid1")
	@Schema(description = "uid1（更小的uid）")
    private Long uid1;

    @TableField("uid2")
	@Schema(description = "uid2（更大的uid）")
    private Long uid2;

	@Schema(description = "房间key由两个uid拼接，先做排序uid1_uid2")
    @TableField("room_key")
    private String roomKey;

	@Schema(description = "房间状态 0正常 1屏蔽  uid1 屏蔽 uid2")
	private Boolean deFriend1;

	@Schema(description = "房间状态 0正常 1屏蔽 uid2 屏蔽 uid1")
	private Boolean deFriend2;

	@Schema(description = "创建时间")
    @TableField("create_time")
    private Date createTime;

	@Schema(description = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
