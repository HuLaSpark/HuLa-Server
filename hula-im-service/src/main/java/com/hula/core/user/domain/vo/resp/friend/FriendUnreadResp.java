package com.hula.core.user.domain.vo.resp.friend;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 好友校验
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendUnreadResp extends BaseEntity {

    @Schema(description ="申请列表的未读数")
    private Integer unReadCount;

}
