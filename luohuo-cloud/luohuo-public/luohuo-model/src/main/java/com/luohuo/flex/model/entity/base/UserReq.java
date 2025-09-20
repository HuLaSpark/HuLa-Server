package com.luohuo.flex.model.entity.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户列表
 * @author 乾乾
 */
@Data
@NoArgsConstructor
public class UserReq implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Long> uidList;
}