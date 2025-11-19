package com.luohuo.basic.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import com.luohuo.basic.converter.String2DateConverter;
import com.luohuo.basic.exception.BizException;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static cn.hutool.core.date.DatePattern.CHINESE_DATE_PATTERN;
import static cn.hutool.core.date.DatePattern.CHINESE_DATE_TIME_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_TIME_PATTERN;
import static com.luohuo.basic.exception.BaseException.BASE_VALID_PARAM;

/**
 * 描述：日期工具类
 *
 * @author 乾乾
 * 修改时间：2018/4/24
 */
@Slf4j
public final class DateUtils {
    public static final String DEFAULT_YEAR_FORMAT = "yyyy";
    public static final String DEFAULT_MONTH_FORMAT = "yyyy-MM";
    public static final String DEFAULT_MONTH_FORMAT_SLASH = "yyyy/MM";
    public static final String DEFAULT_MONTH_FORMAT_EN = "yyyy年MM月";
    public static final String DEFAULT_WEEK_FORMAT = "yyyy-ww";
    public static final String DEFAULT_WEEK_FORMAT_EN = "yyyy年ww周";
    public static final String DEFAULT_DATE_FORMAT = NORM_DATE_PATTERN;
    public static final String DEFAULT_DATE_FORMAT_EN = CHINESE_DATE_PATTERN;
    public static final String DEFAULT_DATE_TIME_FORMAT = NORM_DATETIME_PATTERN;
    public static final String DEFAULT_DATE_TIME_START_FORMAT = "yyyy-MM-dd 00:00:00";
    public static final String DEFAULT_DATE_TIME_END_FORMAT = "yyyy-MM-dd 23:59:59";
    public static final String DEFAULT_DATE_TIME_FORMAT_EN = CHINESE_DATE_TIME_PATTERN;
    public static final String DEFAULT_TIME_FORMAT = NORM_TIME_PATTERN;
    public static final String DEFAULT_TIME_EN_FORMAT = "HH时mm分ss秒";
    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String WEEK = "WEEK";

    public static final String DEFAULT_DATE_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    public static final String DEFAULT_DATE_TIME_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String DEFAULT_MONTH_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月$";
    public static final String DEFAULT_DATE_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日$";
    public static final String DEFAULT_DATE_TIME_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$";
    public static final String SLASH_DATE_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2}$";
    public static final String SLASH_DATE_TIME_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String SLASH_DATE_FORMAT = "yyyy/MM/dd";
    public static final String SLASH_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String CRON_FORMAT = "ss mm HH dd MM ? yyyy";

    public static final Map<String, String> LOCAL_DATE_TIME_FORMAT_MAP = new LinkedHashMap<>(10);
    public static final Map<String, String> DATE_TIME_FORMAT_MAP = new LinkedHashMap<>(15);
    public static final Map<String, String> LOCAL_DATE_FORMAT_MAP = new LinkedHashMap<>(10);
    public static final Map<String, String> LOCAL_TIME_FORMAT_MAP = new LinkedHashMap<>(10);

    static {
        // 日期时间
        LOCAL_DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_TIME_FORMAT_MATCHES);
        LOCAL_DATE_TIME_FORMAT_MAP.put(SLASH_DATE_TIME_FORMAT, SLASH_DATE_TIME_FORMAT_MATCHES);
        LOCAL_DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_TIME_FORMAT_EN, DEFAULT_DATE_TIME_FORMAT_EN_MATCHES);
        LOCAL_DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        LOCAL_DATE_TIME_FORMAT_MAP.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        LOCAL_DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);

        // 日期
        LOCAL_DATE_FORMAT_MAP.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        LOCAL_DATE_FORMAT_MAP.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        LOCAL_DATE_FORMAT_MAP.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);

        // 时间
        LOCAL_TIME_FORMAT_MAP.put(DEFAULT_TIME_FORMAT, "^\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        LOCAL_TIME_FORMAT_MAP.put(DEFAULT_TIME_EN_FORMAT, "^\\d{1,2}时\\d{1,2}分\\d{1,2}秒$");

        // 日期时间
        DATE_TIME_FORMAT_MAP.put(DEFAULT_YEAR_FORMAT, "^\\d{4}");
        DATE_TIME_FORMAT_MAP.put(DEFAULT_MONTH_FORMAT, "^\\d{4}-\\d{1,2}$");
        DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        DATE_TIME_FORMAT_MAP.put("yyyy-MM-dd HH", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}");
        DATE_TIME_FORMAT_MAP.put("yyyy-MM-dd HH:mm", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_TIME_FORMAT_MATCHES);
        DATE_TIME_FORMAT_MAP.put(DEFAULT_MONTH_FORMAT_SLASH, "^\\d{4}/\\d{1,2}$");
        DATE_TIME_FORMAT_MAP.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        DATE_TIME_FORMAT_MAP.put("yyyy/MM/dd HH", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}");
        DATE_TIME_FORMAT_MAP.put("yyyy/MM/dd HH:mm", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        DATE_TIME_FORMAT_MAP.put(SLASH_DATE_TIME_FORMAT, SLASH_DATE_TIME_FORMAT_MATCHES);
        DATE_TIME_FORMAT_MAP.put(DEFAULT_MONTH_FORMAT_EN, DEFAULT_MONTH_FORMAT_EN_MATCHES);
        DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);
        DATE_TIME_FORMAT_MAP.put(DEFAULT_DATE_TIME_FORMAT_EN, DEFAULT_DATE_TIME_FORMAT_EN_MATCHES);
    }

    /**
     * 一个月平均天数
     */
    public static final long MAX_MONTH_DAY = 30;
    /**
     * 3个月平均天数
     */
    public static final long MAX_3_MONTH_DAY = 90;
    /**
     * 一年平均天数
     */
    public static final long MAX_YEAR_DAY = 365;
    private static final Map<String, String> DATE_FORMAT = new LinkedHashMap<>(5);
//--格式化日期start-----------------------------------------

    static {
        DATE_FORMAT.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        DATE_FORMAT.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        DATE_FORMAT.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);
    }

    private DateUtils() {
    }

    /**
     * 将字符串解析LocalDate
     *
     * @param source 源参数
     *               支持以下格式：
     *               yyyy-MM-dd
     *               yyyy/MM/dd
     *               yyyy年MM月dd日
     * @return 日期
     */
    public static LocalDate parse(String source) {
        String sourceTrim = source.trim();
        Set<Map.Entry<String, String>> entries = DATE_FORMAT.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (sourceTrim.matches(entry.getValue())) {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern(entry.getKey()));
            }
        }
        throw BizException.wrap("解析日期失败, 请传递正确的日期格式");
    }


    /**
     * 转换 Date 为 cron , eg：  "0 07 10 15 1 ? 2016"
     *
     * @param date 时间点
     * @return cron 表达式
     */
    public static String getCron(Date date) {
        return format(date, CRON_FORMAT);
    }

    /**
     * 转换 LocalDateTime 为 cron , eg.  "0 07 10 15 1 ? 2016"
     *
     * @param date 时间点
     * @return cron 表达式
     */
    public static String getCron(LocalDateTime date) {
        return format(date, CRON_FORMAT);
    }

    /**
     * 格式化日期,返回格式为 yyyy-MM-mm HH:mm:ss
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime date) {
        return format(date, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 格式化日期,
     *
     * @param date    日期
     * @param pattern 格式, 默认值为 yyyy-MM-mm HH:mm:ss
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime date, String pattern) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        if (pattern == null) {
            pattern = DEFAULT_DATE_TIME_FORMAT;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化日期,返回格式为 yyyy-MM
     *
     * @param date 日期
     * @return 格式化后的字符串
     */

    public static String format(LocalDate date, String pattern) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (pattern == null) {
            pattern = DEFAULT_MONTH_FORMAT;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 根据传入的格式格式化日期.默认格式为MM月dd日
     *
     * @param d 日期
     * @param f 格式
     * @return 格式化后的字符串
     */
    public static String format(Date d, String f) {
        Date date = d;
        String format = f;
        if (date == null) {
            date = new Date();
        }
        if (format == null) {
            format = DEFAULT_DATE_TIME_FORMAT;
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 格式化日期,返回格式为 yyyy-MM-dd
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsDate(LocalDateTime date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }

    public static String formatAsDate(LocalDate date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }

    public static String formatAsDateEn(LocalDateTime date) {
        return format(date, DEFAULT_DATE_FORMAT_EN);
    }


    public static String formatAsYearMonth(LocalDateTime date) {
        return format(date, DEFAULT_MONTH_FORMAT);
    }

    public static String formatAsYearMonthEn(LocalDateTime date) {
        return format(date, DEFAULT_MONTH_FORMAT_EN);
    }

    /**
     * 格式化日期,返回格式为 yyyy-ww
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsYearWeek(LocalDateTime date) {
        return format(date, DEFAULT_WEEK_FORMAT);
    }

    public static String formatAsYearWeekEn(LocalDateTime date) {
        return format(date, DEFAULT_WEEK_FORMAT_EN);
    }

    /**
     * 格式化日期,返回格式为 yyyy-MM
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsYearMonth(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_MONTH_FORMAT);
        return df.format(date);
    }

    /**
     * 格式化日期,返回格式为 yyyy-ww
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsYearWeek(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_WEEK_FORMAT);
        return df.format(date);
    }

    /**
     * 格式化日期,返回格式为 HH:mm:ss 例:12:24:24
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        return df.format(date);
    }

    /**
     * 格式化日期,返回格式为 yyyy-MM-dd
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return df.format(date);
    }

    /**
     * 格式化日期,返回格式为 yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsDateTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        return df.format(date);
    }


    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 格式化日期,返回格式为 dd ,即对应的天数.
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatAsDay(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return df.format(date);
    }

    //--格式化日期end-----------------------------------------

    //--解析日期start-----------------------------------------

    /**
     * 将字符转换成日期
     *
     * @param dateStr 日期字符串
     * @param format  解析格式
     * @return 解析后的日期
     */
    public static Date parse(String dateStr, String format) {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            date = dateFormat.parse(dateStr);

        } catch (Exception e) {
            log.info("DateUtils error", e);
        }
        return date;
    }

    /**
     * 获取当月最后一天
     *
     * @param date 日期
     * @return 当月最后一天
     */
    public static Date getLastDateOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return calendar.getTime();
    }

    /**
     * 根据传入的String返回对应的date
     *
     * @param source 日期字符串
     * @return 日期
     */
    public static Date parseAsDate(String source) {
        String sourceTrim = source.trim();
        Set<Map.Entry<String, String>> entries = DATE_FORMAT.entrySet();
        try {
            for (Map.Entry<String, String> entry : entries) {
                if (sourceTrim.matches(entry.getValue())) {
                    return new SimpleDateFormat(entry.getKey()).parse(source);
                }
            }
        } catch (ParseException e) {
            throw BizException.wrap("解析日期失败, 请传递正确的日期格式");
        }
        throw BizException.wrap("解析日期失败, 请传递正确的日期格式");
    }

    private static <T> T convert(String source, Map<String, String> format, Function<String, T> function) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        String sourceTrim = source.trim();
        Set<Map.Entry<String, String>> entries = format.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (sourceTrim.matches(entry.getValue())) {
                return function.apply(entry.getKey());
            }
        }
        throw new IllegalArgumentException("无效的日期参数格式:'" + sourceTrim + "'");
    }

    public static void main(String[] args) {
        System.out.println(parseAsDateTime("2012-12-14 22:22:33"));
        System.out.println(parseAsDateTime("2012-12-14 22:22"));
        System.out.println(parseAsDateTime("2012-12-14 22"));
        System.out.println(parseAsDateTime("2012-01-22"));

        System.out.println(parseAsDateTime("2012-01-22"));
        System.out.println(parseAsDateTime("2012/01/22"));

//        System.out.println(parseAsDateTime("22:22:33"));
//        System.out.println(parseAsDateTime("22时22分33秒"));


        System.out.println(new String2DateConverter().convert("2012-12-14 22:22:33"));
        System.out.println(new String2DateConverter().convert("2012-01-22"));

        System.out.println(new String2DateConverter().convert("2012-01-22"));
        System.out.println(new String2DateConverter().convert("2012/01/22"));
//        System.out.println(new String2DateConverter().convert("2012/01=22"));

//        System.out.println(new String2DateConverter().convert("22:22:33"));
//        System.out.println(new String2DateConverter().convert("22时22分33秒"));
    }

    /**
     * 按给定参数返回Date对象
     *
     * @param dateTime 时间对象格式为("yyyy-MM-dd HH:mm:ss");
     * @return 解析后的日期
     */
    public static Date parseAsDateTime(String dateTime) {
        Function<String, Date> function = format -> {
            try {
                DateFormat dateFormat = new SimpleDateFormat(format);
                //严格模式
                dateFormat.setLenient(false);
                return dateFormat.parse(dateTime);
            } catch (ParseException e) {
                log.info("转换日期失败, date={}, format={}", dateTime, format, e);
                throw new BizException(BASE_VALID_PARAM, e.getMessage(), e);
            }
        };
        return convert(dateTime, DATE_TIME_FORMAT_MAP, function);
    }


    public static LocalDate parseAsLocalDate(String source) {
        return convert(source, LOCAL_DATE_FORMAT_MAP, key -> LocalDate.parse(source, DateTimeFormatter.ofPattern(key)));
    }

    public static LocalTime parseAsLocalTime(String source) {
        return convert(source, LOCAL_TIME_FORMAT_MAP, key -> LocalTime.parse(source, DateTimeFormatter.ofPattern(key)));
    }

    public static LocalDateTime parseAsLocalDateTime(String source) {
        Function<String, LocalDateTime> function = key -> {
            if (source.matches(DEFAULT_DATE_FORMAT_MATCHES)
                    || source.matches(DEFAULT_DATE_FORMAT_EN_MATCHES)
                    || source.matches(SLASH_DATE_FORMAT_MATCHES)
            ) {
                return LocalDateTime.of(LocalDate.parse(source, DateTimeFormatter.ofPattern(key)), LocalTime.MIN);
            }
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(key));
        };

        return convert(source, LOCAL_DATE_TIME_FORMAT_MAP, function);
    }


    /**
     * 获取指定日期的开始时间
     * 如：00:00:00
     *
     * @param value 日期
     * @return 解析后的日期
     */
    public static Date getDate0000(LocalDateTime value) {
        return getDate0000(value.toLocalDate());
    }

    /**
     * 获取指定日期的开始时间
     * 如：00:00:00
     *
     * @param value 日期
     * @return 解析后的日期
     */
    public static Date getDate0000(Date value) {
        return getDate0000(DateUtils.date2LocalDate(value));
    }

    /**
     * 获取指定日期的开始时间
     * 如：00:00:00
     *
     * @param value 日期
     * @return 解析后的日期
     */
    public static Date getDate0000(LocalDate value) {
        LocalDateTime todayStart = LocalDateTime.of(value, LocalTime.MIN);
        return DateUtils.localDateTime2Date(todayStart);
    }

    /**
     * 获取指定日期的结束时间
     * 如：23:59:59
     *
     * @param value 日期
     * @return 解析后的日期
     */
    public static Date getDate2359(LocalDateTime value) {
        return getDate2359(value.toLocalDate());

    }

    /**
     * 获取指定日期的结束时间
     * 如：23:59:59
     *
     * @param value 日期
     * @return 解析后的日期
     */
    public static Date getDate2359(Date value) {
        return getDate2359(DateUtils.date2LocalDate(value));
    }

    /**
     * 获取指定日期的结束时间
     * 如：23:59:59
     *
     * @param value 日期
     * @return 解析后的日期
     */
    public static Date getDate2359(LocalDate value) {
        LocalDateTime dateEnd = LocalDateTime.of(value, LocalTime.MAX);
        return DateUtils.localDateTime2Date(dateEnd);
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime 日期
     * @return 解析后的日期
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    //--解析日期 end-----------------------------------------


    /**
     * Date转换为LocalDateTime
     *
     * @param date 日期
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        if (date == null) {
            return LocalDateTime.now();
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 日期转 LocalDate
     *
     * @param date 日期
     * @return 解析后的日期
     */
    public static LocalDate date2LocalDate(Date date) {
        if (date == null) {
            return LocalDate.now();
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 日期转 LocalTime
     *
     * @param date 日期
     * @return 解析后的日期
     */
    public static LocalTime date2LocalTime(Date date) {
        if (date == null) {
            return LocalTime.now();
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalTime();
    }


    /**
     * 毫秒转日期
     *
     * @param epochMilli 毫秒
     * @return 解析后的日期
     */
    public static LocalDateTime getDateTimeOfTimestamp(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 秒转日期
     *
     * @param epochSecond 秒
     * @return 解析后的日期
     */
    public static LocalDateTime getDateTimeOfSecond(long epochSecond) {
        Instant instant = Instant.ofEpochSecond(epochSecond);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    //-计算日期 start------------------------------------------


    /**
     * 计算结束时间与当前时间间隔的天数
     *
     * @param endDate 结束日期
     * @return 计算结束时间与当前时间间隔的天数
     */
    public static long until(Date endDate) {
        return LocalDateTime.now().until(date2LocalDateTime(endDate), ChronoUnit.DAYS);
    }

    /**
     * 计算结束时间与开始时间间隔的天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 计算结束时间与开始时间间隔的天数
     */
    public static long until(Date startDate, Date endDate) {
        return date2LocalDateTime(startDate).until(date2LocalDateTime(endDate), ChronoUnit.DAYS);
    }


    /**
     * 计算结束时间与开始时间间隔的天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 计算结束时间与开始时间间隔的天数
     */
    public static long until(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.until(endDate, ChronoUnit.DAYS);
    }

    public static long until(LocalDate startDate, LocalDate endDate) {
        return startDate.until(endDate, ChronoUnit.DAYS);
    }

    /**
     * 计算2个日期之间的所有的日期 yyyy-MM-dd
     * 含头含尾
     *
     * @param start yyyy-MM-dd
     * @param end   yyyy-MM-dd
     * @return 日期区间的所有日期
     */
    public static List<String> getBetweenDay(Date start, Date end) {
        return getBetweenDay(date2LocalDate(start), date2LocalDate(end));
    }

    /**
     * 计算2个日期之间的所有的日期 yyyy-MM-dd
     * 含头含尾
     *
     * @param start yyyy-MM-dd
     * @param end   yyyy-MM-dd
     */
    public static List<String> getBetweenDay(String start, String end) {
        return getBetweenDay(LocalDate.parse(start), LocalDate.parse(end));
    }

    /**
     * 计算2个日期之间的所有的日期 yyyy-MM-dd
     * 含头含尾
     *
     * @param startDate yyyy-MM-dd
     * @param endDate   yyyy-MM-dd
     */
    public static List<String> getBetweenDay(LocalDate startDate, LocalDate endDate) {
        return getBetweenDay(startDate, endDate, DEFAULT_DATE_FORMAT);
    }

    public static List<String> getBetweenDayEn(LocalDate startDate, LocalDate endDate) {
        return getBetweenDay(startDate, endDate, DEFAULT_DATE_FORMAT_EN);
    }

    public static List<String> getBetweenDay(LocalDate startDate, LocalDate endDate, String pattern) {
        if (pattern == null) {
            pattern = DEFAULT_DATE_FORMAT;
        }
        List<String> list = new ArrayList<>();
        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }
        String finalPattern = pattern;
        Stream.iterate(startDate, d -> d.plusDays(1)).
                limit(distance + 1)
                .forEach(f -> list.add(f.format(DateTimeFormatter.ofPattern(finalPattern))));
        return list;
    }


    /**
     * 计算2个日期之间的所有的周 yyyy-ww
     * 含头含尾
     *
     * @param start yyyy-MM-dd
     * @param end   yyyy-MM-dd
     */
    public static List<String> getBetweenWeek(Date start, Date end) {
        return getBetweenWeek(date2LocalDate(start), date2LocalDate(end));
    }

    /**
     * 计算2个日期之间的所有的周 yyyy-ww
     * 含头含尾
     *
     * @param start yyyy-MM-dd
     * @param end   yyyy-MM-dd
     * @return 2个日期之间的所有的周
     */
    public static List<String> getBetweenWeek(String start, String end) {
        return getBetweenWeek(LocalDate.parse(start), LocalDate.parse(end));
    }

    /**
     * 计算2个日期之间的所有的周 yyyy-ww
     * 含头含尾
     *
     * @param startDate yyyy-MM-dd
     * @param endDate   yyyy-MM-dd
     * @return 2个日期之间的所有的周
     */
    public static List<String> getBetweenWeek(LocalDate startDate, LocalDate endDate) {
        return getBetweenWeek(startDate, endDate, DEFAULT_WEEK_FORMAT);
    }

    public static List<String> getBetweenWeek(LocalDate startDate, LocalDate endDate, String pattern) {
        List<String> list = new ArrayList<>();

        long distance = ChronoUnit.WEEKS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusWeeks(1)).
                limit(distance + 1).forEach(f -> list.add(f.format(DateTimeFormatter.ofPattern(pattern))));
        return list;
    }

    /**
     * 计算2个日期之间的所有的月 yyyy-MM
     *
     * @param start yyyy-MM-dd
     * @param end   yyyy-MM-dd
     * @return 2个日期之间的所有的月
     */
    public static List<String> getBetweenMonth(Date start, Date end) {
        return getBetweenMonth(date2LocalDate(start), date2LocalDate(end));
    }

    /**
     * 计算2个日期之间的所有的月 yyyy-MM
     *
     * @param start yyyy-MM-dd
     * @param end   yyyy-MM-dd
     * @return 2个日期之间的所有的月
     */
    public static List<String> getBetweenMonth(String start, String end) {
        return getBetweenMonth(LocalDate.parse(start), LocalDate.parse(end));
    }

    /**
     * 计算2个日期之间的所有的月 yyyy-MM
     *
     * @param startDate yyyy-MM-dd
     * @param endDate   yyyy-MM-dd
     * @return 2个日期之间的所有的月
     */
    public static List<String> getBetweenMonth(LocalDate startDate, LocalDate endDate) {
        return getBetweenMonth(startDate, endDate, DEFAULT_MONTH_FORMAT);
    }

    public static List<String> getBetweenMonth(LocalDate startDate, LocalDate endDate, String pattern) {
        List<String> list = new ArrayList<>();
        long distance = ChronoUnit.MONTHS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }

        Stream.iterate(startDate, d -> d.plusMonths(1))
                .limit(distance + 1)
                .forEach(f -> list.add(f.format(DateTimeFormatter.ofPattern(pattern))));
        return list;
    }

    /**
     * 计算时间区间内的日期列表，并返回
     *
     * @param startTime 开始
     * @param endTime   结束
     * @param dateList  日期
     * @return 计算时间区间内的日期列表
     */
    public static String calculationEn(LocalDateTime startTime, LocalDateTime endTime, List<String> dateList) {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (endTime == null) {
            endTime = LocalDateTime.now().plusDays(30);
        }
        return calculationEn(startTime.toLocalDate(), endTime.toLocalDate(), dateList);
    }

    public static String calculation(LocalDate startDate, LocalDate endDate, List<String> dateList) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(30);
        }
        if (dateList == null) {
            dateList = new ArrayList<>();
        }
        long day = until(startDate, endDate);

        String dateType;
        if (day >= 0 && day <= MAX_MONTH_DAY) {
            dateType = DAY;
            dateList.addAll(DateUtils.getBetweenDay(startDate, endDate, DEFAULT_DATE_FORMAT));
        } else if (day > MAX_MONTH_DAY && day <= MAX_3_MONTH_DAY) {
            dateType = WEEK;
            dateList.addAll(DateUtils.getBetweenWeek(startDate, endDate, DEFAULT_WEEK_FORMAT));
        } else if (day > MAX_3_MONTH_DAY && day <= MAX_YEAR_DAY) {
            dateType = MONTH;
            dateList.addAll(DateUtils.getBetweenMonth(startDate, endDate, DEFAULT_MONTH_FORMAT));
        } else {
            throw new BizException("日期参数只能介于0-365天之间");
        }
        return dateType;
    }

    public static String calculationEn(LocalDate startDate, LocalDate endDate, List<String> dateList) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(30);
        }
        if (dateList == null) {
            dateList = new ArrayList<>();
        }
        long day = until(startDate, endDate);

        String dateType;
        if (day >= 0 && day <= MAX_MONTH_DAY) {
            dateType = DAY;
            dateList.addAll(DateUtils.getBetweenDay(startDate, endDate, DEFAULT_DATE_FORMAT_EN));
        } else if (day > MAX_MONTH_DAY && day <= MAX_3_MONTH_DAY) {
            dateType = WEEK;
            dateList.addAll(DateUtils.getBetweenWeek(startDate, endDate, DEFAULT_WEEK_FORMAT_EN));
        } else if (day > MAX_3_MONTH_DAY && day <= MAX_YEAR_DAY) {
            dateType = MONTH;
            dateList.addAll(DateUtils.getBetweenMonth(startDate, endDate, DEFAULT_MONTH_FORMAT_EN));
        } else {
            throw new BizException("日期参数只能介于0-365天之间");
        }
        return dateType;
    }

//----------//----------//----------//----------//----------//----------//----------//----------//----------//----------//----------

    /**
     * 计算开始时间
     *
     * @param time 日期
     * @return 计算开始时间
     */
    public static LocalDateTime getStartTime(String time) {
        String startTime = time;
        if (time.matches("^\\d{4}-\\d{1,2}$")) {
            startTime = time + "-01 00:00:00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            startTime = time + " 00:00:00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            startTime = time + ":00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
            startTime = time.replace("T", " ").substring(0, time.indexOf('.'));
        }
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
    }

    /**
     * 计算结束时间
     *
     * @param time 日期
     * @return 结束时间 精确到毫秒
     */
    public static LocalDateTime getEndTime(String time) {
        String startTime = time;
        if (time.matches("^\\d{4}-\\d{1,2}$")) {
            Date date = DateUtils.parse(time, "yyyy-MM");
            date = DateUtils.getLastDateOfMonth(date);
            startTime = DateUtils.formatAsDate(date) + " 23:59:59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            startTime = time + " 23:59:59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            startTime = time + ":59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
            time = time.replace("T", " ").substring(0, time.indexOf('.'));
            startTime = time;
        }

        return endOfDay(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
    }

    public static LocalDateTime endOfDay(LocalDateTime time) {
        return time.with(LocalTime.of(23, 59, 59, 999_999_000));
    }

    /**
     * 判断当前时间是否在指定时间范围
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 结果
     */
    public static boolean between(LocalTime from, LocalTime to) {
        if (from == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }
        if (to == null) {
            throw new IllegalArgumentException("结束时间不能为空");
        }
        LocalTime now = LocalTime.now();
        return now.isAfter(from) && now.isBefore(to);
    }

    /**
     * 转换日期
     * <p>
     * 0: 今天结束的日期
     * 1m: 1分钟后的日期
     * 1h: 1小时后的日期
     * 4d: 4天后的日期
     * 2w: 2周后的日期
     * 3M: 3个月后的日期
     * 5y: 5年后的日期
     *
     * @param dateTime 待转换日期
     * @param time     转换格式 如：
     *                 0 当天23:59:59
     *                 1s 1秒后
     *                 3m 3分钟后
     *                 2w 2周后
     *                 1h 1小时后
     *                 2H 2小时后
     *                 4d 4天后
     *                 5M 5月后
     *                 6y 6年后
     * @return 日期
     */
    public static LocalDateTime conversionDateTime(LocalDateTime dateTime, String time, Integer lockUserTime) {
        if (StrUtil.isEmpty(time)) {
            return LocalDateTime.MAX;
        }

        if (dateTime == null) {
            return endOfDay(LocalDateTime.now());
        }

        // 今天的23:59:59
        if (StrPool.ZERO.equals(time)) {
            return dateTime.plusMinutes(lockUserTime);
//			return endOfDay(dateTime);
        }

        char unit = Character.toLowerCase(time.charAt(time.length() - 1));
        if (time.length() == 1) {
            unit = 'd';
        }
        Long lastTime = Convert.toLong(time.substring(0, time.length() - 1));

        return switch (unit) {
            //秒
            case 's' -> dateTime.plusSeconds(lastTime);
            //分
            case 'm' -> dateTime.plusMinutes(lastTime);
            //时
            case 'h' | 'H' -> dateTime.plusHours(lastTime);
            //周
            case 'w' -> dateTime.plusWeeks(lastTime);
            //月
            case 'M' -> dateTime.plusMonths(lastTime);
            //年
            case 'y' -> dateTime.plusYears(lastTime);
            //天
            default -> dateTime.plusDays(lastTime);
        };
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }


    /**
     * 转换时间显示方式
     * @param duration 时间
     * @return
     */
    public static String tranDurationToShow(Duration duration) {

        StringBuilder result = new StringBuilder();
        long days = Math.abs(duration.toDays());
        if (days >= 1) {
            result.append(days).append("天");
        }
        long hours = Math.abs(duration.toHours());
        if (hours >= 1) {
            if (days >= 1) {
                long l = hours % 24;
                if (l > 0) {
                    result.append(l).append("小时");
                }
                return result.append(duration.toHours() > 0 ? "前" : "后").toString();
            }
            result.append(hours).append("小时");
        }
        long minutes = Math.abs(duration.toMinutes());
        if (minutes >= 1) {
            if (hours >= 1) {
                long l = minutes % 60;
                if (l > 0) {
                    result.append(l).append("分钟");
                }
                return result.append(duration.toMinutes() > 0 ? "前" : "后").toString();
            }
            result.append(minutes).append("分钟");
        }
        if (result.isEmpty()) {
            result.append("1分钟内");
            return result.toString();
        } else {
            return result.append(duration.toSeconds() > 0 ? "前" : "后").toString();
        }
    }
}
