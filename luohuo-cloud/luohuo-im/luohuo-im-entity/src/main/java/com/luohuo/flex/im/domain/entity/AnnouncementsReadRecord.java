package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 公告是否已读表
 */
@Data
@TableName("im_announcements_read_records")
public class AnnouncementsReadRecord extends SuperEntity<Long> {

	@Schema(description = "公告ID")
    private Long announcementsId;

    @Schema(description = "阅读人ID")
	private Long uid;

    @Schema(description = "是否已读 0：未读 1：已读")
    private Boolean isCheck;
}