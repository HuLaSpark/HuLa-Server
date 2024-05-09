package com.hula.core.user.service.impl;

import com.hula.MinIOTemplate;
import com.hula.common.utils.AssertUtil;
import com.hula.core.user.domain.enums.OssSceneEnum;
import com.hula.core.user.domain.vo.req.oss.UploadUrlReq;
import com.hula.core.user.service.OssService;
import com.hula.domain.OssReq;
import com.hula.domain.OssResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author nyh
 **/
@Service
public class OssServiceImpl implements OssService {
    @Resource
    private MinIOTemplate minIOTemplate;

    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum, "场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
