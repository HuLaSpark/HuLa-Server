package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 朋友圈表
 */
@TableName("feed")
@Data
public class Feed implements Serializable {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

    @Schema(description = "发布人id")
	private Long uid;

    @Schema(description = "朋友圈文案")
	private String content;

	@Schema(description = "privacy -> 私密 open -> 公开 partVisible -> 部分可见 notAnyone -> 不给谁看")
	private String permission;

	@Schema(description = "朋友圈内容类型（0: 纯文字 1: 图片, 2: 视频）")
	private Integer mediaType;

	@Schema(description = "创建时间")
	private LocalDateTime createdTime;
}