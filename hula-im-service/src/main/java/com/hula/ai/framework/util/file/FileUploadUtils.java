package com.hula.ai.framework.util.file;

import cn.hutool.core.util.IdUtil;
import com.hula.ai.common.constant.AiConstants;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.common.exception.FileException;
import com.hula.ai.common.utils.DateUtil;
import com.hula.ai.framework.config.SystemConfig;
import com.hula.exception.BizException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * 文件上传工具类
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class FileUploadUtils {

    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 200 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final FileInfo upload(MultipartFile file) {
        try {
            return upload(SystemConfig.uploadPath, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("文件上传失败");
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public static final FileInfo upload(String baseDir, MultipartFile file) {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("文件上传失败");
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws IOException                          比如读写文件出错时
     */
    public static final FileInfo upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws IOException {
        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileException("上传文件名长度最大为：" + FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }
        assertAllowed(file, allowedExtension);
        String fileName = extractFilename(file);
        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc);
        String pathFileName = getPathFileName(baseDir, fileName);

        // 计算属性
        int newFileNameSeparatorIndex = pathFileName.lastIndexOf("/");
        String newFileName = pathFileName.substring(newFileNameSeparatorIndex + 1).toLowerCase();
        int separatorIndex = newFileName.lastIndexOf(".");
        String suffix = newFileName.substring(separatorIndex + 1).toLowerCase();
        // 计算文件大小信息
        long size = file.getSize();
        String fileSizeInfo = "0kB";
        if (size != 0) {
            String[] unitNames = new String[]{"B", "kB", "MB", "GB", "TB", "EB"};
            int digitGroups = Math.min(unitNames.length - 1, (int) (Math.log10(size) / Math.log10(1024)));
            fileSizeInfo = new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + unitNames[digitGroups];
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getOriginalFilename());
        fileInfo.setFilePath(pathFileName);
        fileInfo.setFileUrl(pathFileName);
        fileInfo.setSize(fileSizeInfo);
        fileInfo.setType(suffix);
        return fileInfo;
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        String extension = getExtension(file);
        String fileName = DateUtil.formatLocalDate(LocalDate.now(), "yyyy/MM") + StringPoolConstant.SLASH + IdUtil.fastUUID() + "." + extension;
        return fileName;
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

    public static final String getPathFileName(String uploadDir, String fileName) {
        String currentDir = uploadDir.replace(SystemConfig.uploadPath, StringPoolConstant.EMPTY);
		return AiConstants.RESOURCE_PREFIX + StringPoolConstant.SLASH + currentDir + fileName;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @return
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
            throw new FileException("文件大小超出限制：" + DEFAULT_MAX_SIZE / 1024 / 1024);
        }
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new FileException("文件类型不符合");
            } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new FileException("文件类型不符合");
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new FileException("文件类型不符合");
            } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new FileException("文件类型不符合");
            } else {
                throw new FileException("文件类型不符合");
            }
        }

    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }
}
