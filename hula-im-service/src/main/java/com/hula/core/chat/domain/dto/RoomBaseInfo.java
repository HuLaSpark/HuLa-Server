package com.hula.core.chat.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 房间详情
 * @author nyh
 */
@Data
public class RoomBaseInfo {
    @Schema(description ="房间id")
    private Long roomId;
    @Schema(description ="会话名称")
    private String name;
    @Schema(description ="会话头像")
    private String avatar;
    /**
     * 房间类型 1群聊 2单聊
     */
    private Integer type;

    private Integer hotFlag;

    /**
     * 群最后消息的更新时间
     */
    @TableField("active_time")
    private Date activeTime;

    /**
     * 最后一条消息id
     */
    @TableField("last_msg_id")
    private Long lastMsgId;
}
