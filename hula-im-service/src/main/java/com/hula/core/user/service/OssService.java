package com.hula.core.user.service;


import com.hula.core.user.domain.vo.req.oss.UploadUrlReq;
import com.hula.domain.vo.res.OssResp;

/**
 * <p>
 * oss 服务类
 * </p>
 *
 * @author nyh
 */
public interface OssService {

    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
