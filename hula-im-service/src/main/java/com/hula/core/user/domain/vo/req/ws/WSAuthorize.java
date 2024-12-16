package com.hula.core.user.domain.vo.req.ws;

import com.hula.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSAuthorize extends BaseEntity {
    private String token;
}
