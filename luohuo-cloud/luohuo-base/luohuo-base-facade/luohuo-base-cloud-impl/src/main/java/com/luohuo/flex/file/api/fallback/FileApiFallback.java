package com.luohuo.flex.file.api.fallback;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.luohuo.basic.base.R;
import com.luohuo.flex.file.api.FileApi;
import com.luohuo.flex.file.enumeration.FileStorageType;
import com.luohuo.flex.file.vo.result.FileResultVO;

/**
 * 熔断
 *
 * @author 乾乾
 * @date 2019/07/25
 */
@Component
public class FileApiFallback implements FileApi {
    @Override
    public R<FileResultVO> upload(MultipartFile file, String bizType, String bucket, FileStorageType storageType) {
        return R.timeout();
    }

}
