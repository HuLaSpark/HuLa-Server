package com.luohuo.flex.im.domain.vo.resp.friend;

import com.luohuo.flex.im.domain.enums.ApplyStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


/**
 * 好友校验
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyResp {
    @Schema(description ="申请id")
    private Long applyId;

    @Schema(description ="申请人uid")
    private Long uid;

	@Schema(description = "被申请人uid、申请的roomId")
	private Long targetId;

    @Schema(description ="申请类型 1加好友 2群聊")
    private Integer type;

    @Schema(description ="申请信息")
    private String msg;

	/**
	 * 申请状态
	 * @see ApplyStatusEnum
	 */
    @Schema(description ="申请状态")
    private Integer status;

	@Schema(description ="申请时间")
	private LocalDateTime createTime;
}
