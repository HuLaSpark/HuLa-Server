package com.hula.ai.common.utils;

import com.hula.ai.common.enums.OperateSystemEnum;

/**
 * 获取操作系统工具类
 *
 * @author: 云裂痕
 * @date: 2023/3/22 10:27
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class OperateSystemUtil {

    private static String OS = System.getProperty("os.name").toLowerCase();

    private static OperateSystemUtil instance = new OperateSystemUtil();

    private OperateSystemEnum platform;

    private OperateSystemUtil() {
    }

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public static boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public static boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public static boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public static boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public static boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public static boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public static boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public static boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public static boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public static boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public static boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public static OperateSystemEnum getOSname() {
        if (isAix()) {
            instance.platform = OperateSystemEnum.AIX;
        } else if (isDigitalUnix()) {
            instance.platform = OperateSystemEnum.Digital_Unix;
        } else if (isFreeBSD()) {
            instance.platform = OperateSystemEnum.FreeBSD;
        } else if (isHPUX()) {
            instance.platform = OperateSystemEnum.HP_UX;
        } else if (isIrix()) {
            instance.platform = OperateSystemEnum.Irix;
        } else if (isLinux()) {
            instance.platform = OperateSystemEnum.Linux;
        } else if (isMacOS()) {
            instance.platform = OperateSystemEnum.Mac_OS;
        } else if (isMacOSX()) {
            instance.platform = OperateSystemEnum.Mac_OS_X;
        } else if (isMPEiX()) {
            instance.platform = OperateSystemEnum.MPEiX;
        } else if (isNetWare()) {
            instance.platform = OperateSystemEnum.NetWare_411;
        } else if (isOpenVMS()) {
            instance.platform = OperateSystemEnum.OpenVMS;
        } else if (isOS2()) {
            instance.platform = OperateSystemEnum.OS2;
        } else if (isOS390()) {
            instance.platform = OperateSystemEnum.OS390;
        } else if (isOSF1()) {
            instance.platform = OperateSystemEnum.OSF1;
        } else if (isSolaris()) {
            instance.platform = OperateSystemEnum.Solaris;
        } else if (isSunOS()) {
            instance.platform = OperateSystemEnum.SunOS;
        } else if (isWindows()) {
            instance.platform = OperateSystemEnum.Windows;
        } else {
            instance.platform = OperateSystemEnum.Others;
        }
        return instance.platform;
    }

}


