package com.hula.core.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 修改用户名
 *
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummeryInfoDTO {
    @Schema(description = "用户拥有的徽章id列表")
    List<Long> itemIds;
    @Schema(description = "用户id")
    private Long uid;
    @Schema(description = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;
    @Schema(description = "用户昵称")
    private String name;
	@Schema(description = "Hula号")
	private String account;
    @Schema(description = "用户头像")
    private String avatar;
    @Schema(description = "归属地")
    private String locPlace;
	@Schema(description = "用户状态")
	private Long userStateId;
    @Schema(description = "佩戴的徽章id")
    private Long wearingItemId;
	@Schema(description = "用户类型")
	private Integer userType;

    public static SummeryInfoDTO skip(Long uid) {
        SummeryInfoDTO dto = new SummeryInfoDTO();
        dto.setUid(uid);
        dto.setNeedRefresh(Boolean.FALSE);
        return dto;
    }
}
