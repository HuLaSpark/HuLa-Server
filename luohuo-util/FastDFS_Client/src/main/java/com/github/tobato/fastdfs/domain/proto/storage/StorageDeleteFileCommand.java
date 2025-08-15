package com.github.tobato.fastdfs.domain.proto.storage;

import com.github.tobato.fastdfs.domain.proto.AbstractFdfsCommand;
import com.github.tobato.fastdfs.domain.proto.FdfsResponse;
import com.github.tobato.fastdfs.domain.proto.storage.internal.StorageDeleteFileRequest;

/**
 * 文件删除命令
 *
 * @author tobato
 */
public class StorageDeleteFileCommand extends AbstractFdfsCommand<Void> {

    /**
     * 文件删除命令
     *
     */
    public StorageDeleteFileCommand(String groupName, String path) {
        super();
        this.request = new StorageDeleteFileRequest(groupName, path);
        // 输出响应
        this.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
