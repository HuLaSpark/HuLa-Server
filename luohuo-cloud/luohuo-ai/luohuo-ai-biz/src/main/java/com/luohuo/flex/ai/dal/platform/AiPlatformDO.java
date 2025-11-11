package com.luohuo.flex.ai.dal.platform;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.flex.ai.dal.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AI 平台配置 DO
 *
 * @author 乾乾
 */
@TableName("ai_platform")
@KeySequence("ai_platform_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AiPlatformDO extends BaseDO {

	/**
	 * 编号
	 */
	@TableId
	private Long id;

	/**
	 * 平台代码
	 */
	private String platform;

	/**
	 * 平台名称
	 */
	private String name;

	/**
	 * 平台显示标签
	 */
	private String label;

	/**
	 * 模型示例
	 */
	private String examples;

	/**
	 * 文档链接
	 */
	private String docs;

	/**
	 * 提示信息
	 */
	private String hint;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 状态
	 *
	 * 0 - 启用
	 * 1 - 禁用
	 */
	private Integer status;

}