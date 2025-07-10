package com.hula.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    private Long createId;

    private Date createTime;

    private Long updateId;

    private Date updateTime;

}
