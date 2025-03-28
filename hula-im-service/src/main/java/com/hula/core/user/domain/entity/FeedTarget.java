package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 朋友圈标签、用户可见表
 */
@TableName("feed_target")
@Data
public class FeedTarget implements Serializable {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

    @Schema(description = "朋友圈ID")
    private Long feedId;

	/**
	 * @see Target
	 */
    @Schema(description = "标签ID")
    private Long targetId;

	@Schema(description = "1 -> 关联标签id 2 -> 关联用户id")
	private Integer type;
}