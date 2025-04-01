package com.hula.ai.framework.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.common.utils.DateUtil;
import com.hula.ai.common.utils.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用处理工具类
 *
 * @author: 云裂痕
 * @date: 2019/8/28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Slf4j
public class CommonUtil {

    private static final char[] DIGIT = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 根据时间戳生成交易单号
     *
     * @param type   0：退款 1：支付 2:查询
     * @return 32位商户单号:20210615154740140470716103616151
     */
    public static String getTransactionNumber(int type) {
        String str = DateUtil.getCurrentDateTimeShort();
        StringBuffer sb = new StringBuffer();
        sb.append(str).append(String.valueOf(SnowFlakeUtil.snowflakeId()).substring(0, 16));
        int sum = 0;
        for (int i = 0; i < sb.length(); i++) {
            sum = sum + Integer.valueOf(sb.substring(i, i + 1));
        }
        int s = sum % 9;
        sb.append(s).append(type);
        return sb.toString().trim();
    }

    /**
     * 根据时间戳生成内部合同号
     *
     * @param param 开头 合同号 ：2位首字母
     * @return 后台单号: 例如 调拨：DB2003071055270759
     */
    public static String getContractNumber(String param) {
        String str = DateUtil.formatLocalDateTime(LocalDateTime.now(), "yyyyMMddHHmm");
        StringBuffer sb = new StringBuffer();
        sb.append(param).append(str).append(RandomUtil.randomNumbers(4));
        return sb.toString().trim();
    }

    /**
     * 截取字符串
     *
     * @param str 字符串
     * @param len 长度
     * @return
     */
    public static String subStr(String str, Integer len) {
        if (str == null) {
            return StringPoolConstant.EMPTY;
        }
        Integer lg = str.length();
        if (len > lg) {
            return str.substring(0, lg);
        } else {
            return str.substring(0, len);
        }
    }

    /**
     * 将特定字符串分割的参数转成数组
     *
     * @param str   字符串
     * @return
     */
    public static List<Integer> fromStringToList(String str) {
        return fromStringToList(str, StringPoolConstant.COMMA);
    }

    /**
     * 将特定字符串分割的参数转成数组
     *
     * @param str   字符串
     * @param param 分割参数
     * @return
     */
    public static List<Integer> fromStringToList(String str, String param) {
        if (ObjectUtil.isNull(str)) {
            return new ArrayList<>();
        }
        String[] rsIds = str.split(param);
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < rsIds.length; i++) {
            ids.add(Integer.parseInt(rsIds[i]));
        }
        return ids;
    }

    /**
     * 将特定字符串分割的参数转成数组
     *
     * @param str 字符串
     * @return
     */
    public static List<String> fromStringToListStr(String str) {
        return fromStringToListStr(str, StringPoolConstant.COMMA);
    }

    /**
     * 将特定字符串分割的参数转成数组
     *
     * @param str 字符串
     * @return
     */
    public static List<String> fromStringToListStr(String str, String param) {
        if (ObjectUtil.isNull(str)) {
            return new ArrayList<>();
        }
        String[] rsIds = str.split(param);
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < rsIds.length; i++) {
            ids.add(rsIds[i]);
        }
        return ids;
    }

    /**
     * 将特点字符串分割的参数转成数组
     *
     * @param arr 数组
     * @return 字符串
     */
    public static String fromListToString(List<Integer> arr) {
        String ids = arr.toString();
        ids = ids.substring(1, ids.length() - 1).replaceAll("\\s*", StringPoolConstant.EMPTY);
        return ids;
    }

    /**
     * 姓名脱敏 只显示第一个汉字，其他隐藏为2个星号，比如：李**
     *
     * @param fullName 姓名
     * @return
     */
    public static String nameEncrypt(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return fullName;
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * 手机号码前三后四脱敏
     *
     * @param mobile 手机号
     * @return
     */
    public static String mobileEncrypt(String mobile) {
        if (ObjectUtil.isNull(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2");
    }

    /**
     * 固定电话 后四位，其他隐藏，比如1234
     *
     * @return
     */
    public static String phoneEncrypt(String phone) {
        if (StringUtils.isBlank(phone)) {
            return phone;
        }
        return StringUtils.leftPad(StringUtils.right(phone, 4), StringUtils.length(phone), "*");
    }

    /**
     * 身份证前三后四脱敏
     *
     * @param id 身份证号
     * @return
     */
    public static String idEncrypt(String id) {
        if (ObjectUtil.isNull(id) || (id.length() < 8)) {
            return id;
        }
        return id.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }

    /**
     * 护照脱敏
     *
     * @param passport
     * @return
     */
    public static String passportEncrypt(String passport) {
        if (ObjectUtil.isNull(passport) || (passport.length() < 8)) {
            return passport;
        }
        return passport.substring(0, 2) + new String(new char[passport.length() - 5]).replace("\0", "*") + passport.substring(passport.length() - 3);
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address
     * @param sensitiveSize 敏感信息长度
     * @return
     */
    public static String addressEncrypt(String address, int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return "";
        }
        int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
    }

    /**
     * 电子邮箱 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com
     *
     * @param email 邮箱
     * @return
     */
    public static String emailEncrypt(String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }

    /**
     * 银行卡号前4位，后3位，其他用星号隐藏每位1个星号，比如：6217 **** **** **** 567>
     *
     * @param cardNum 银行卡号
     * @return
     */
    public static String bankCardEncrypt(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return cardNum;
        }
        return StringUtils.left(cardNum, 4).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 3), StringUtils.length(cardNum), "*"), "****"));
    }

    /**
     * 字符串正则匹配
     *
     * @param p
     * @param str
     * @return
     */
    public static String matchResult(Pattern p, String str) {
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                sb.append(m.group());
            }
        }
        return sb.toString();
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray 字节数组
     * @return
     */
    static String byteToStr(byte[] byteArray) {
        String strDigest = StringPoolConstant.EMPTY;
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte 字节
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] tempArr = new char[2];
        tempArr[0] = DIGIT[(mByte >>> 4) & 0X0F];
        tempArr[1] = DIGIT[mByte & 0X0F];
        String item = new String(tempArr);
        return item;
    }

    /**
     * 抽奖方法
     * create time: 2019/7/5 23:08
     *
     * @param orignalRates 商品中奖概率列表，保证顺序和实际物品对应
     * @return 中奖商品索引
     */
    public static int lottery(List<Double> orignalRates) {
        if (orignalRates == null || orignalRates.isEmpty()) {
            return -1;
        }
        int size = orignalRates.size();

        // 计算总概率，这样可以保证不一定总概率是1
        double sumRate = 0d;
        for (double rate : orignalRates) {
            sumRate += rate;
        }

        // 计算每个物品在总概率的基础下的概率情况
        List<Double> sortOrignalRates = new ArrayList<Double>(size);
        Double tempSumRate = 0d;
        for (double rate : orignalRates) {
            tempSumRate += rate;
            sortOrignalRates.add(tempSumRate / sumRate);
        }
        // 根据区块值来获取抽取到的物品索引
        double nextDouble = Math.random();
        sortOrignalRates.add(nextDouble);
        Collections.sort(sortOrignalRates);
        return sortOrignalRates.indexOf(nextDouble);
    }

}
