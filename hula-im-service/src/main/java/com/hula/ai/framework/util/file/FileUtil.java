package com.hula.ai.framework.util.file;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.common.enums.ResponseEnum;
import com.hula.ai.common.exception.FileException;
import com.hula.ai.common.utils.DateUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Base64;

/**
 * 文件工具类
 *
 * @author: 云裂痕
 * @date: 2023/9/15
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class FileUtil {

    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * fileToInput
     *
     * @return
     */
    public static InputStream fileToInput(File file) {
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] byt = new byte[input.available()];
            input.read(byt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input;
    }

    /**
     * buf to InputStream
     *
     * @param buf
     * @return
     */
    public static InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    /**
     * inStream to byte[]
     *
     * @param inStream
     * @return
     * @throws IOException
     */
    public static byte[] input2byte(InputStream inStream) {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * File to byte[]
     *
     * @return
     * @throws IOException
     */
    public static byte[] FileToByte(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            // 强转成int类型大小的数组
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            fis.close();
            return fileBytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * MultipartFile2File
     *
     * @param multipartFile
     * @return
     */
    public static File transferToFile(MultipartFile multipartFile) {
        //选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            //获取文件名
            String originalFilename = multipartFile.getOriginalFilename();
            //获取最后一个"."的位置
            int cutPoint = originalFilename.lastIndexOf(StringPoolConstant.DOT);
            //获取文件名
            String prefix = originalFilename.substring(0, cutPoint);
            //获取后缀名
            String suffix = originalFilename.substring(cutPoint);
            //创建临时文件
            file = File.createTempFile(prefix, suffix);
            //multipartFile2file
            multipartFile.transferTo(file);
            //删除临时文件
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 将base64字符串，生成文件
     *
     * @param base64   base64文件
     * @param filePath 文件存储路径
     * @param fileName 文件名称
     */
    public static File convertBase64ToFile(String base64, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            File dir = new File(filePath);
            //判断文件目录是否存在
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }

            byte[] bfile = Base64.getDecoder().decode(base64);

            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 远程图片转File
     *
     * @param url     图片链接
     * @return
     */
    public static File convertUrlToFile(String url, String filePath, String fileName) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            if (conn.getResponseCode() != ResponseEnum.SUCCESS.getCode().intValue()) {
                return null;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(filePath + fileName);
            InputStream is = conn.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            FileOutputStream os = new FileOutputStream(file, true);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.close();
            is.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            conn.disconnect();
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
    public static final String upload(String baseDir, MultipartFile file) {
        return upload(baseDir, file, true);

    }

    /**
     * 文件上传
     *
     * @param baseDir         相对应用的基目录
     * @param file            上传的文件
     * @param customeFileName 是否自定义文件名称
     * @return 返回上传成功的文件名
     */
    public static final String upload(String baseDir, MultipartFile file, Boolean customeFileName) {
        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > DEFAULT_FILE_NAME_LENGTH) {
            throw new FileException();
        }
        assertAllowed(file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        String fileName = extractFilename(file, customeFileName);
        try {
            File desc = getAbsoluteFile(baseDir, fileName);
            file.transferTo(desc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String pathFileName = getPathFileName(baseDir, fileName);
        return pathFileName;
    }


    /**
     * 本地文件（图片、excel等）转换成Base64字符串
     *
     */
    public static String convertFileToBase64(File file) {
        return convertFileToBase64(cn.hutool.core.io.FileUtil.getInputStream(file));
    }

    /**
     * 本地文件（图片、excel等）转换成Base64字符串
     *
     * @param imgPath 文件路径
     */
    public static String convertFileToBase64(String imgPath) {
        try {
            InputStream in = new FileInputStream(imgPath);
            return convertFileToBase64(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 本地文件（图片、excel等）转换成Base64字符串
     *
     */
    public static String convertFileToBase64(InputStream in) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组进行Base64编码，得到Base64编码的字符串
        String base64Str = Base64.getEncoder().encodeToString(data);
        return base64Str;
    }

    /**
     * 以流的形式下载文件
     *
     * @param response
     * @param path     文件路径
     * @return
     */
    public static HttpServletResponse download(HttpServletResponse response, String path) {
        // path是指欲下载的文件的路径。
        File file = new File(path);
        try {
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, MediaType.ALL.getType());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            file.delete();
        }
        return response;
    }

    /**
     * 下载本地文件
     *
     * @param response
     * @param path     文件存储路径
     */
    public static void downloadLocal(HttpServletResponse response, String path) {
        // 读到流中
        // 文件的存放路径
        // path是指欲下载的文件的路径。
        File file = new File(path);
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 设置输出的格式
        response.reset();
        response.setContentType("bin");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        try {
            while ((len = inStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }

    /**
     * 下载网络url文件
     *
     * @param response
     * @param netUrl   网络地址
     * @param filename 文件存储本地地址
     * @throws MalformedURLException
     */
    public void downloadNet(HttpServletResponse response, String netUrl, String filename) throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;
        URL url = new URL(netUrl);
        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(filename);
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件大小
     *
     * @param size 文件大小
     * @return
     */
    public static String getFileSize(long size) {
        if (0 == size) {
            return "0kB";
        }
        String[] unitNames = new String[]{"B", "kB", "MB", "GB", "TB", "EB"};
        int digitGroups = Math.min(unitNames.length - 1, (int) (Math.log10(size) / Math.log10(1024)));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + unitNames[digitGroups];
    }

    /**
     * 获取文件类型
     * <p>
     * 例如: 文章.txt, 返回: txt
     *
     * @param file 文件名
     * @return 后缀（不含".")
     */
    public static String getFileType(File file) {
        if (null == file) {
            return StringUtils.EMPTY;
        }
        return getFileType(file.getName());
    }

    /**
     * 获取文件类型
     * <p>
     * 例如: 文章.txt, 返回: txt
     *
     * @param fileName 文件名
     * @return 后缀（不含".")
     */
    public static String getFileType(String fileName) {
        int separatorIndex = fileName.lastIndexOf(StringPoolConstant.DOT);
        if (separatorIndex < 0) {
            return StringPoolConstant.EMPTY;
        }
        return fileName.substring(separatorIndex + 1).toLowerCase();
    }

    /**
     * 获取文件类型
     *
     * @param photoByte 文件字节码
     * @return 后缀（不含".")
     */
    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = "JPG";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97)) {
            strFileExtendName = "GIF";
        } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) {
            strFileExtendName = "JPG";
        } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
            strFileExtendName = "BMP";
        } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
            strFileExtendName = "PNG";
        }
        return strFileExtendName;
    }

    /**
     * 编码文件名
     *
     * @param file            文件
     * @param customeFileName 是否自定义文件名称
     */
    public static final String extractFilename(MultipartFile file, Boolean customeFileName) {
        String extension = getExtension(file);
        if (!customeFileName) {
            return file.getOriginalFilename();
        }
        if (Arrays.asList(MimeTypeUtils.CERT_EXTENSION).contains(extension)) {
            return IdUtil.fastUUID() + StringPoolConstant.DOT + extension;
        }
        return DateUtil.getCurrentDate(DateUtil.DATE_FORMATTER_SLASH) + StringPoolConstant.SLASH + IdUtil.fastUUID() + StringPoolConstant.DOT + extension;

    }

    /**
     * 获取文件夹路径名称
     */
    public static final String getPathName(String path, String defaultPath) {
        if (ObjectUtil.isNull(path)) {
            return defaultPath;
        }
        if (StringPoolConstant.SLASH.equals(path.substring(path.length() - 1))) {
            return path;
        }
        return path + StringPoolConstant.SLASH;
    }

    /**
     * 获取文件
     *
     * @param uploadDir
     * @param fileName
     * @return
     */
    public static final File getAbsoluteFile(String uploadDir, String fileName) {
        File desc = new File(uploadDir + File.separator + fileName);
        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

    /**
     * 获取文件路径名称
     *
     * @param uploadDir
     * @param fileName
     * @return
     */
    public static final String getPathFileName(String uploadDir, String fileName) {
        int dirLastIndex = uploadDir.length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        String pathFileName = currentDir + fileName;
        return pathFileName;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @return
     */
    private static final void assertAllowed(MultipartFile file, String[] allowedExtension) {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
            throw new FileException("超出文件上传限制，无法上传");
        }
        if (allowedExtension != null && !isAllowedExtension(getExtension(file), allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new FileException("允许的文件类型：" + allowedExtension);
            } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new FileException("允许的文件类型：" + allowedExtension);
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new FileException("允许的文件类型：" + allowedExtension);
            } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new FileException("允许的文件类型：" + allowedExtension);
            } else {
                throw new FileException("允许的文件类型：" + allowedExtension);
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
