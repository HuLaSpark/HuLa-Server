package com.luohuo.flex.im.domain.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知视图对象
 */
@Data
@Schema(description = "通知视图对象")
public class NoticeVO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "事件类型:1-好友申请;2-群申请;3-群事件")
	private Integer eventType;

	@Schema(description = "通知类型 1群聊 2加好友")
	private Integer type;

	@Schema(description = "申请ID")
	private Long applyId;

	@Schema(description = "被操作的人")
	private Long operateId;

	@Schema(description = "房间id")
	private Long roomId;

    @Schema(description = "通知内容 申请时填写的")
    private String content;
    
    @Schema(description = "处理状态:0-未处理;1-已同意;2-已拒绝;3-忽略")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "是否已读")
    private Integer read;
    
    @Schema(description = "发送人名称")
    private String senderName;
    
    @Schema(description = "发送人头像")
    private String senderAvatar;
    
    @Schema(description = "接收人名称")
    private String receiverName;
    
    @Schema(description = "接收人头像")
    private String receiverAvatar;
    
    @Schema(description = "发送人ID")
    private Long senderId;
    
    @Schema(description = "接收人ID")
    private Long receiverId;
}