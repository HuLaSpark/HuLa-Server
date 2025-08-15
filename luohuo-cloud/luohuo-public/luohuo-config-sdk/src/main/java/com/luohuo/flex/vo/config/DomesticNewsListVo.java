package com.luohuo.flex.vo.config;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 新闻集合的返回参数
 */
@Data
public class DomesticNewsListVo implements Serializable {

	private String id;

	private LocalDateTime ctime;

	private String title;

	private String description;

	private String source;

	private String picUrl;

	private String url;
}
