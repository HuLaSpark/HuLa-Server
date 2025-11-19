package com.luohuo.basic.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 集合类型的 event
 *
 * @author qianqian
 * @date 2024年01月07日17:25:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class ListDTO implements Serializable {
    private static final long serialVersionUID = -3124612657759050173L;

	@Schema(description = "值")
	private List<String> value;

	private Long oldOrgId;

	private String newTreePath;


}
