package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 朋友圈发的图片或者视频表
 */
@TableName("feed_media")
@Data
public class FeedMedia implements Serializable {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

    @Schema(description = "朋友圈ID")
    private Long feedId;

	@Schema(description = "图片或视频的路径")
	private String url;

    @Schema(description = "排序")
    private Integer sort;
}