package com.hula.ai.framework.util.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件映射信息
 *
 * @author: 云裂痕
 * @date: 2025/03/08
 * 得其道 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse implements Serializable {

    private static final long serialVersionUID = 1L;

	/**
	 * 文件id
	 */
	private String fileId;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件类型
	 */
	private String fileType;
}
