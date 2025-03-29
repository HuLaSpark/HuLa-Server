package com.hula.ai.framework.util.file;

import com.hula.ai.common.constant.StringPoolConstant;

/**
 * 媒体类型工具类
 *
 * @author: 云裂痕
 * @date: 2023/9/15
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class MimeTypeUtils {
    public static final String IMAGE_PNG = "image/png";

    public static final String IMAGE_JPG = "image/jpg";

    public static final String IMAGE_JPEG = "image/jpeg";

    public static final String IMAGE_BMP = "image/bmp";

    public static final String IMAGE_GIF = "image/gif";

    public static final String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};

    public static final String[] FLASH_EXTENSION = {"swf", "flv"};

    public static final String[] MEDIA_EXTENSION = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb"};

    public static final String[] VIDEO_EXTENSION = {"mp4", "avi", "rmvb"};

    public static final String[] FILE_EXTENSION = {"doc", "docx", "xls", "xlsx", "pdf", "ppt", "pptx", "html", "htm", "txt"};

    public static final String[] CODE_EXTENSION = {"sql", "java", "jsp", "vue", "php", "py", "js", "css"};

    public static final String[] COMPRESSION_EXTENSION = {"rar", "zip", "gz", "bz2"};

    /**
     * 证书文件类型
     */
    public static final String[] CERT_EXTENSION = {"crt", "pem", "p12", "pfx"};

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // 音频
            "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb",
            // 视频格式
            "mp4", "avi", "rmvb",
            // 文档格式
            "doc", "docx", "xls", "xlsx", "pdf", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 代码文件
            "sql", "java", "jsp", "vue", "php", "py", "js", "css",
            // 证书格式
            "crt", "txt", "pem", "p12",
    };

    public static String getExtension(String prefix) {
        switch (prefix) {
            case IMAGE_PNG:
                return "png";
            case IMAGE_JPG:
                return "jpg";
            case IMAGE_JPEG:
                return "jpeg";
            case IMAGE_BMP:
                return "bmp";
            case IMAGE_GIF:
                return "gif";
            default:
                return StringPoolConstant.EMPTY;
        }
    }

}
