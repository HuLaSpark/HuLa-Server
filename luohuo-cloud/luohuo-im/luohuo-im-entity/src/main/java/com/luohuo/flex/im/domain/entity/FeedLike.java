package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 朋友圈点赞表
 */
@Data
@TableName("im_feed_like")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedLike implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 朋友圈ID
	 */
	@Schema(description = "朋友圈ID")
	@TableField("feed_id")
	private Long feedId;

	/**
	 * 点赞人uid
	 */
	@Schema(description = "点赞人uid")
	@TableField("uid")
	private Long uid;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	@TableField("create_time")
	private LocalDateTime createTime;

	/**
	 * 创建者
	 */
	@Schema(description = "创建者")
	@TableField("create_by")
	private Long createBy;
}
