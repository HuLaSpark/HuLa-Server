package com.luohuo.flex.im.domain.vo.resp.friend;

import com.luohuo.basic.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 好友校验
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendCheckResp extends BaseEntity {

    @Schema(description ="校验结果")
    private List<FriendCheck> checkedList;

    @Data
    public static class FriendCheck {
        private Long uid;
        private Boolean isFriend;
    }

}
