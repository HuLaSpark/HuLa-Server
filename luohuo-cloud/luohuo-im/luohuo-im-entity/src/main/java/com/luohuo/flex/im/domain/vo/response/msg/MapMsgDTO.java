package com.luohuo.flex.im.domain.vo.response.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地图消息返回体
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapMsgDTO {
	@Schema(description = "纬度")
	private String latitude;

	@Schema(description = "经度")
	private String longitude;

	@Schema(description = "详细地址")
	private String address;

	@Schema(description = "定位精度")
	private String precision;

	@Schema(description = "时间戳")
	private String timestamp;
}
