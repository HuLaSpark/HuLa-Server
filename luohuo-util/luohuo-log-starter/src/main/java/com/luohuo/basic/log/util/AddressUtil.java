package com.luohuo.basic.log.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import com.luohuo.basic.utils.StrPool;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * 根据ip查询地址
 *
 * @author 乾乾
 * @date 2019/10/30
 */
@Slf4j
public final class AddressUtil {

    private static Searcher searcher = null;

    static {
        try {
            URL resource = AddressUtil.class.getResource("/ip2region/ip2region.xdb");
            if (resource != null) {
                String dbPath = resource.getPath();
                File file = new File(dbPath);
                if (!file.exists()) {
                    String tmpDir = System.getProperties().getProperty(StrPool.JAVA_TEMP_DIR);
                    dbPath = tmpDir + "ip2region.xdb";
                    file = new File(dbPath);
                    String classPath = "classpath:ip2region/ip2region.xdb";
                    InputStream resourceAsStream = ResourceUtil.getStreamSafe(classPath);
                    if (resourceAsStream != null) {
                        FileUtils.copyInputStreamToFile(resourceAsStream, file);
                    }
                }
                // 1、从 dbPath 加载整个 xdb 到内存。
                byte[] cBuff = Searcher.loadContentFromFile(dbPath);
                searcher = Searcher.newWithBuffer(cBuff);

                log.info("bean [{}]", searcher);
            }
        } catch (Exception e) {
            log.error("init ip region error", e);
        }
    }

    private AddressUtil() {
    }

    /**
     * 解析IP
     *
     * @param ip ip
     * @return 地区
     */
    public static String getRegion(String ip) {
        try {
            //db
            if (searcher == null || StrUtil.isEmpty(ip)) {
                log.error("DbSearcher is null");
                return StrUtil.EMPTY;
            }
            long startTime = System.currentTimeMillis();

            String result = searcher.search(ip);
            long endTime = System.currentTimeMillis();
            log.debug("region use time[{}] result[{}]", endTime - startTime, result);
            return result;

        } catch (Exception e) {
            log.error("根据ip查询地区失败:", e);
        }
        return StrUtil.EMPTY;
    }


}
