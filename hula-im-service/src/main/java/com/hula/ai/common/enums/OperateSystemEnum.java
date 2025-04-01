package com.hula.ai.common.enums;

import lombok.Getter;

/**
 * 运行操作系统枚举
 *
 * @author: 云裂痕
 * @date: 2023/3/22 10:27
 * @version: 1.0.0
 * 得其道 乾乾
 */
@Getter
public enum OperateSystemEnum {

    /**
     * 操作系统简称
     */
    Any("any"),
    Linux("Linux"),
    Mac_OS("Mac OS"),
    Mac_OS_X("Mac OS X"),
    Windows("Windows"),
    OS2("OS/2"),
    Solaris("Solaris"),
    SunOS("SunOS"),
    MPEiX("MPE/iX"),
    HP_UX("HP-UX"),
    AIX("AIX"),
    OS390("OS/390"),
    FreeBSD("FreeBSD"),
    Irix("Irix"),
    Digital_Unix("Digital Unix"),
    NetWare_411("NetWare"),
    OSF1("OSF1"),
    OpenVMS("OpenVMS"),
    Others("Others");

    private final String value;

    private OperateSystemEnum(final String value) {
        this.value = value;
    }

}
