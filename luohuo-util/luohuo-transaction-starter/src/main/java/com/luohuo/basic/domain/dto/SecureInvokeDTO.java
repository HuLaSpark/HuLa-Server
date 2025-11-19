package com.luohuo.basic.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Description:
 * Date: 2023-08-06
 */

@Data
@Builder
public class SecureInvokeDTO {
    private String className;
    private String methodName;
    private String parameterTypes;
    private String args;

	public SecureInvokeDTO() {
	}

	public SecureInvokeDTO(String className, String methodName, String parameterTypes, String args) {
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
}
