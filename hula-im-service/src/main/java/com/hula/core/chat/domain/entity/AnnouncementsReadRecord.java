package com.hula.core.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 公告是否已读表
 */
@Data
@TableName("announcements_read_records")
public class AnnouncementsReadRecord implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@Schema(description = "公告ID")
    private Long announcementsId;

    @Schema(description = "阅读人ID")
	private Long uid;

    @Schema(description = "是否已读 0：未读 1：已读")
    private Boolean isCheck;
}