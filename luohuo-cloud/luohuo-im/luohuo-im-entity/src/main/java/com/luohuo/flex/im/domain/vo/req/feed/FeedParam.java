package com.luohuo.flex.im.domain.vo.req.feed;

import com.luohuo.flex.im.domain.enums.FeedEnum;
import com.luohuo.flex.im.domain.enums.FeedPermissionEnum;
import com.luohuo.flex.model.vo.query.OperParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 发布朋友圈的参数
 */
@Data
public class FeedParam extends OperParam {

	@NotNull(message = "请选择朋友圈", groups = {Update.class})
	private Long id;

	@Schema(description = "朋友圈文案")
	@Size(min = 1, max = 2000, message = "朋友圈文案必须在1到2000个字符之间")
	private String content;

	/**
	 * @see FeedEnum
	 */
	@Schema(description = "发布内容的类型")
	@Min(value = 0, message = "请选择正确的素材类型")
	@Max(value = 2, message = "请选择正确的素材类型")
	private Integer mediaType;

	@Schema(description = "发布的图片的url")
	private List<String> images;

	@Schema(description = "发布视频的url")
	private String videoUrl;

	/**
	 * @see FeedPermissionEnum
	 */
	@Schema(description = "privacy -> 私密 open -> 公开 partVisible -> 部分可见 notAnyone -> 不给谁看")
	@Pattern(regexp = "^(privacy|open|partVisible|notAnyone)$", message = "请选择正确的可见类型（privacy/open/partVisible/notAnyone）")
	private String permission;

	@Schema(description = "permission 限制的用户id")
	private List<Long> uidList;

	@Schema(description = "permission 限制的标签id")
	private List<Long> targetIds;
}
