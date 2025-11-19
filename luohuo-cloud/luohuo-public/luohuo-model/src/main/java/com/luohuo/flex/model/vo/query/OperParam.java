package com.luohuo.flex.model.vo.query;

import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * OperParam 操作类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OperParam implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 保存和缺省验证组
	 */
	public interface Save extends Default {

	}

	/**
	 * 更新和缺省验证组
	 */
	public interface Update extends Default {

	}

	/**
	 * 删除和缺省验证组
	 */
	public interface Delete extends Default {

	}
}
