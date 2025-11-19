package com.luohuo.flex.file.facade;


import org.springframework.web.multipart.MultipartFile;
import com.luohuo.flex.file.enumeration.FileStorageType;
import com.luohuo.flex.file.vo.result.FileResultVO;

/**
 * 文件接口
 *
 * @author 乾乾
 * @since 2024年09月20日10:37:25
 */
public interface FileFacade {

    /**
     * 通过feign-form 实现文件 跨服务上传
     *
     * @param file        文件
     * @param bizType     业务类型
     * @param bucket      桶
     * @param storageType 存储类型
     * @return 文件信息
     */
    FileResultVO upload(MultipartFile file, String bizType, String bucket, FileStorageType storageType);

}
