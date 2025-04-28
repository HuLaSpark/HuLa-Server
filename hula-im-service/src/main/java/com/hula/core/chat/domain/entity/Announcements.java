package com.hula.core.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天公告
 */
@Data
@TableName("announcements")
public class Announcements implements Serializable {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@Schema(description = "群id")
	private Long roomId;

	@Schema(description = "公告发布人")
	private Long uid;

	@Schema(description = "发布内容")
	private String content;

	@Schema(description = "发布时间")
	private Date createdTime;

	@Schema(description = "更新时间")
	private Date updatedTime;

	@Schema(description = "置顶")
	private Boolean top;
}
