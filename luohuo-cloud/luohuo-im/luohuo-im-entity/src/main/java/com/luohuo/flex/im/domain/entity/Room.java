package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luohuo.basic.base.entity.TenantEntity;
import com.luohuo.flex.im.domain.enums.HotFlagEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 房间表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_room")
public class Room extends TenantEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 房间类型 1群聊 2单聊
     */
    @TableField("type")
    private Integer type;

    /**
     * 是否全员展示 0否 1是
     */
    @TableField("hot_flag")
    private Integer hotFlag;

    /**
     * 群最后消息的更新时间（热点群不需要写扩散，更新这里就行）
     */
    @TableField("active_time")
    private LocalDateTime activeTime;

    /**
     * 最后一条消息id
     */
    @TableField("last_msg_id")
    private Long lastMsgId;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    @TableField("ext_json")
    private String extJson;

    @JsonIgnore
    public boolean isHotRoom() {
        return HotFlagEnum.of(this.hotFlag) == HotFlagEnum.YES;
    }

    @JsonIgnore
    public boolean isRoomFriend() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.FRIEND;
    }

    @JsonIgnore
    public boolean isRoomGroup() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.GROUP;
    }
}
