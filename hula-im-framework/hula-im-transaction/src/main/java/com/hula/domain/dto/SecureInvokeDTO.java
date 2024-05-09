package com.hula.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nyh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureInvokeDTO {
    private String className;
    private String methodName;
    private String parameterTypes;
    private String args;
}
