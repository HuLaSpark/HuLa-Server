package com.hula;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author nyh
 */
@Data
@AllArgsConstructor
public class OssFile {
    /**
     * OSS 存储时文件路径
     */
    String ossFilePath;
    /**
     * 原始文件名
     */
    String originalFileName;
}