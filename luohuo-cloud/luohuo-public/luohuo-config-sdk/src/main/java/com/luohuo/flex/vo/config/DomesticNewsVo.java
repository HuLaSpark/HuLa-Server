package com.luohuo.flex.vo.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 新闻的返回参数
 */
@Data
public class DomesticNewsVo implements Serializable {

	private Integer curpage;

	private Integer allnum;

	private List<DomesticNewsListVo> newslist;
}
