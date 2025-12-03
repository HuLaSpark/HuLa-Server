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
            URL resource = AddressUtil.class.getResource("/ip2region/ip2region_v4.xdb");
            String dbPath = null;
            if (resource != null) {
                dbPath = resource.getPath();
            } else {
                String[] candidates = new String[] {
                        "install" + File.separator + "ip2region_v4.xdb",
                        ".." + File.separator + "install" + File.separator + "ip2region_v4.xdb",
                        ".." + File.separator + ".." + File.separator + "install" + File.separator + "ip2region_v4.xdb"
                };
                for (String c : candidates) {
                    File f = new File(c);
                    if (f.exists()) {
                        dbPath = f.getAbsolutePath();
                        break;
                    }
                }
            }
            if (dbPath != null) {
                File file = new File(dbPath);
                if (!file.exists()) {
                    String tmpDir = System.getProperties().getProperty(StrPool.JAVA_TEMP_DIR);
                    dbPath = tmpDir + "ip2region_v4.xdb";
                    file = new File(dbPath);
                    String classPath = "classpath:ip2region/ip2region_v4.xdb";
                    InputStream resourceAsStream = ResourceUtil.getStreamSafe(classPath);
                    if (resourceAsStream != null) {
                        FileUtils.copyInputStreamToFile(resourceAsStream, file);
                    }
                }
                byte[] cBuff = Searcher.loadContentFromFile(dbPath);
                searcher = Searcher.newWithBuffer(cBuff);
                log.info("IP地址数据加载完毕 [{}]", searcher);
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
            if (searcher == null || StrUtil.isEmpty(ip)) {
                return StrUtil.EMPTY;
            }
			return searcher.search(ip);
        } catch (Exception e) {
            return StrUtil.EMPTY;
        }
    }

}
