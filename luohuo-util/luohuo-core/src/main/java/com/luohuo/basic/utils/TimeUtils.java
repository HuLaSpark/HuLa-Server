package com.luohuo.basic.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static cn.hutool.core.date.DatePattern.*;

/**
 * 描述：日期工具类
 *
 * @author 乾乾
 * 修改时间：2024/03/20
 */
@Slf4j
public final class TimeUtils {
    public static final String DEFAULT_YEAR_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_MONTH_FORMAT = "yyyy-MM";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DEFAULT_MONTH_FORMAT_SLASH = "yyyy/MM";
    public static final String DEFAULT_MONTH_FORMAT_EN = "yyyy年MM月";
    public static final String DEFAULT_WEEK_FORMAT = "yyyy-ww";
    public static final String DEFAULT_WEEK_FORMAT_EN = "yyyy年ww周";
    public static final String DEFAULT_DATE_FORMAT = NORM_DATE_PATTERN;
    public static final String DEFAULT_DATE_FORMAT_EN = CHINESE_DATE_PATTERN;
    public static final String DEFAULT_DATE_TIME_FORMAT = NORM_DATETIME_PATTERN;
    public static final String DEFAULT_DATE_TIME_START_FORMAT = YYYY_MM_DD + " 00:00:00";
    public static final String DEFAULT_DATE_TIME_END_FORMAT = YYYY_MM_DD + " 23:59:59";
	public static final String DATE_FORMAT_MONTH_START = "yyyy-MM-01 00:00:00";
	public static final String DATE_FORMAT_YEAR_START = "yyyy-01-01 00:00:00";
	public static final String DATE_FORMAT_YEAR_END = "yyyy-12-31 23:59:59";
    public static final String DEFAULT_DATE_TIME_FORMAT_EN = CHINESE_DATE_TIME_PATTERN;
    public static final String DEFAULT_TIME_FORMAT = NORM_TIME_PATTERN;
    public static final String DEFAULT_TIME_EN_FORMAT = "HH时mm分ss秒";
    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String WEEK = "WEEK";
	public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	public static final String SEARCH_DATE_DAY = "today"; //今天
	public static final String SEARCH_DATE_YESTERDAY = "yesterday"; //昨天
	public static final String SEARCH_DATE_LATELY_7 = "lately7"; //最近7天
	public static final String SEARCH_DATE_LATELY_30 = "lately30"; //最近30天
	public static final String SEARCH_DATE_WEEK = "week"; //本周
	public static final String SEARCH_DATE_PRE_WEEK = "preWeek"; //上周
	public static final String SEARCH_DATE_MONTH = "month"; //本月
	public static final String SEARCH_DATE_PRE_MONTH = "preMonth"; //上月
	public static final String SEARCH_DATE_YEAR = "year"; //年
	public static final String SEARCH_DATE_PRE_YEAR = "preYear"; //上一年

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

	/**
	 * 秒转换成毫秒
	 */
	public static final long SECOND_MILLIS = 1000;

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
	public static final String DATE_TIME_FORMAT_NUM = "yyMMddHHmmss";
	private static final Map<String, String> DATE_FORMAT = new LinkedHashMap<>(5);
//--格式化日期start-----------------------------------------

    static {
        DATE_FORMAT.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        DATE_FORMAT.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        DATE_FORMAT.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);
    }

    private TimeUtils() {
    }

	/**
	 * 传入字符串类型的时间 + 格式
	 * @param time
	 */
	public static LocalDateTime parse(String time) {
		return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT).withZone(ZoneId.of("+8")));
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
	/**
	 * 返回 LocalDateTime 的字符串类型
	 * @param time
	 * @return
	 */
	public static String format(final TemporalAccessor time) {
		return formatLocalDateTimeToStr(DEFAULT_DATE_TIME_FORMAT, time);
	}

	public static final String format(final TemporalAccessor time, String format) {
		return formatLocalDateTimeToStr(format, time);
	}


	/**
	 * 传入一个日期格式化字符串
	 * @return 返回当前时间的字符串形式
	 */
	public static final String dateTimeNow(final String format) {
		return parse(now(), format);
	}

	public static final String parse(final LocalDateTime localDateTime, String format) {
		return formatLocalDateTimeToStr(format, localDateTime);
	}

	/**
	 * 传入字符串类型的时间 + 格式
	 * @param time
	 * @param pattern
	 */
	public static LocalDateTime parse(String time, String pattern) {
		return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("+8")));
	}

	/**
	 * 获取当前Date型日期
	 *
	 * @return LocalDateTime() 当前日期
	 */
	public static LocalDateTime now() {
		return LocalDateTime.now();
	}

	public static LocalTime getLocalTime(){
		return LocalTime.now();
	}

	public static LocalDate getLocalDate(){
		return LocalDate.now();
	}

	/**
	 * 获取当前日期
	 *
	 * @return String: yyyy-MM-dd
	 */
	public static String getDate() {
		return dateTimeNow(YYYY_MM_DD);
	}

	/**
	 * localDateTime 转 字符串; 抛出异常则返回当前时间的字符串
	 * @param format
	 * @param localDateTime
	 */
	public static final String formatLocalDateTimeToStr(final String format, final TemporalAccessor localDateTime) {
		try {
			return DateTimeFormatter.ofPattern(format).withZone(ZoneId.of("+8")).format(localDateTime);
		} catch (Exception e) {
			return dateTimeNow(format);
		}
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
     * 计算两个时间差
     */
	public static String getDatePoor(LocalDateTime endDate, LocalDateTime nowDate) {
		long nd = SECOND_MILLIS * 24 * 60 * 60;
		long nh = SECOND_MILLIS * 60 * 60;
		long nm = SECOND_MILLIS * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = getTime(endDate) - getTime(nowDate);
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
	 * 获取LocalDateTime的时间戳
	 *
	 * @return: 13位的时间戳
	 */
	public static long getTime(Date now) {
		return now.getTime();
	}

	/**
	 * 获取LocalDateTime的时间戳
	 *
	 * @return: 13位的时间戳
	 */
	public static long getTime(LocalDateTime now) {
		return now.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
	}

	public static long getTime(LocalDate now) {
		return now.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
	}

	/**
	 * @return: 获取秒级时间戳
	 */
	public static long getTime() {
		return getTime(now());
	}

    //--格式化日期end-----------------------------------------

    //--解析日期start-----------------------------------------

	public static final String addDay(LocalDateTime localDateTime, int num, String pattern) {
		return parse(localDateTime.plusDays(num), pattern);
	}

	/** 获得本周第一天:yyyy-MM-dd HH:mm:ss */
	public static String getWeekStartDay() {
		return format(LocalDate.now().with(DayOfWeek.MONDAY));
	}

	/** 获得本周最后一天:yyyy-MM-dd HH:mm:ss */
	public static String getWeekEndDay() {
		return addDay(getWeekStartDay(), 7, DEFAULT_DATE_TIME_FORMAT);
	}

	/** 获得上周第一天:yyyy-MM-dd HH:mm:ss */
	public static String getLastWeekStartDay() {
		return addDay(getWeekStartDay(), -7, DEFAULT_DATE_TIME_FORMAT);
	}

	/**
	 * 指定日期加上天数后的日期
	 *
	 * @param num     为增加的天数
	 * @param localDateTime 创建时间
	 * @return
	 */
	public static final String addDay(String localDateTime, int num, String pattern) {
		return format(parse(localDateTime, pattern).plusDays(num));
	}

	/** 获得上周最后一天:yyyy-MM-dd HH:mm:ss */
	public static String getLastWeekEndDay() {
		return addDay(getLastWeekStartDay(), 7, DEFAULT_DATE_TIME_FORMAT);
	}

	/** 获得本月第一天:yyyy-MM-dd HH:mm:ss */
	public static String getMonthStartDay() {
		LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
		return format(startOfMonth, DEFAULT_DATE_TIME_FORMAT);
	}

	/** 获得本月最后一天:yyyy-MM-dd HH:mm:ss */
	public static String getMonthEndDay() {
		return format(now().with(TemporalAdjusters.lastDayOfMonth()));
	}

	public static LocalDateTime getMonthEndDay(String time, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		YearMonth yearMonth = YearMonth.parse(time, formatter);
		LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
		return endOfMonth;
	}

	/** 获得上月第一天:yyyy-MM-dd HH:mm:ss */
	public static String getLastMonthStartDay() {
		LocalDate localDate = LocalDate.now();
		LocalDate lastMonthEnd = localDate.plusDays(-localDate.getDayOfMonth());
		return format(LocalDate.of(lastMonthEnd.getYear(), lastMonthEnd.getMonthValue(), 1));
	}

	/** 获得某月第一天:yyyy-MM-dd HH:mm:ss */
	public static String firstDayOfMonth(String monthTime) {
		LocalDate localDate = LocalDate.parse(monthTime + "-01");
		LocalDateTime firstDayOfMonth = localDate.atStartOfDay();
		return format(firstDayOfMonth, DEFAULT_DATE_TIME_FORMAT);
	}

	/** 获得上月最后一天:yyyy-MM-dd HH:mm:ss */
	public static String getLastMonthEndDay() {
		LocalDate lastMonthEnd = LocalDate.now().withDayOfMonth(1).minusDays(1);
		return format(lastMonthEnd, YYYY_MM_DD);
	}

	/**
	 * 获取某个月最后一天
	 * @param monthTime
	 * @return
	 */
	public static String lastDayOfMonth(String monthTime) {
		LocalDate localDate = parseLocalDate(monthTime, YYYY_MM_DD);
		LocalDate with = localDate.with(TemporalAdjusters.lastDayOfMonth());

		return format(with, YYYY_MM_DD);
	}

	/**
	 * 传入字符串类型的时间 + 格式
	 * @param time
	 * @param pattern
	 */
	public static LocalDate parseLocalDate(String time, String pattern) {
		return LocalDate.parse(time, DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("+8")));
	}

	/**
	 *  两个日期之前的相差秒数
	 * @param now 开始日期
	 * @param end 结束日期
	 * @return 相差天数
	 */
	public static long secondBetween(LocalDateTime now,LocalDateTime end){
		Duration duration = Duration.between(now,end);
		return duration.getSeconds();
	}

	/** 获得今年第一天:yyyy-MM-dd HH:mm:ss */
	public static String getYearStartDay() {
		return format(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), YYYY_MM_DD) + " 00:00:00";
	}

	/** 获得今年最后一天:yyyy-MM-dd HH:mm:ss */
	public static String getYearEndDay() {
		return format(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), YYYY_MM_DD) + " 23:59:59";
	}

	/** 获得上年第一天:yyyy-MM-dd HH:mm:ss */
	public static String getLastYearStartDay() {
		return format(LocalDate.now().plusYears(-1).with(TemporalAdjusters.firstDayOfYear()), YYYY_MM_DD) + " 00:00:00";
	}

	/** 获得上年最后一天:yyyy-MM-dd HH:mm:ss */
	public static String getLastYearEndDay() {
		return format(LocalDate.now().plusYears(-1).with(TemporalAdjusters.lastDayOfYear()), YYYY_MM_DD) + " 23:59:59";
	}

	/** 计算同年同一刻 :yyyy-MM-dd HH:mm:ss */
	public static LocalDateTime getSameDateLastYear(LocalDateTime date, Integer num) {
		return date.minusYears(num);
	}

	/**
	 * 可以根据参数去获取到哪年的那个时间的那个月的开始的具体时间
	 * @param inputDate
	 * @param num
	 * @return
	 */
	public static LocalDateTime getLastJanuaryStart(LocalDateTime inputDate, Integer num) {
		// 获取输入日期的前一年
		return LocalDateTime.of(inputDate.minusYears(num).getYear(), Month.JANUARY, 1, 0, 0, 0, 0);
	}

	/**
	 * 获取到时间的最后一天的最后一秒钟 注意和上面的方法配合使用getLastJanuaryStart
	 * @param startTime
	 * @return
	 */
	public static LocalDateTime getLastJanuaryEnd(LocalDateTime startTime){
		// 获取去年 1 月的结尾（即 1 月 31 日 23:59:59.999999999）
		return startTime.withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59);
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
     * 计算结束时间与开始时间间隔的天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 计算结束时间与开始时间间隔的天数
     */
    public static long daysBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.until(endDate, ChronoUnit.DAYS);
    }

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return startDate.until(endDate, ChronoUnit.DAYS);
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
	 * 获取今日字符串格式的日期
	 * @return
	 */
	public static String nowToStr() {
		return formatLocalDateTimeToStr(DEFAULT_DATE_TIME_FORMAT, now());
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
            startTime = TimeUtils.format(TimeUtils.getMonthEndDay(time, "yyyy-MM"));
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
    public static LocalDateTime conversionDateTime(LocalDateTime dateTime, String time) {
        if (StrUtil.isEmpty(time)) {
            return LocalDateTime.MAX;
        }

        if (dateTime == null) {
            return endOfDay(LocalDateTime.now());
        }

        // 今天的23:59:59
        if (StrPool.ZERO.equals(time)) {
            return endOfDay(dateTime);
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
	public static LocalDateTime getServerStartDate() {
		long time = ManagementFactory.getRuntimeMXBean().getStartTime();
		return timestampToLocalDateTime(time / SECOND_MILLIS);
	}

	/**
	 * 时间戳转日期
	 * @param time
	 * @return LocalDateTime
	 */
	public static LocalDateTime timestampToLocalDateTime(Long time) {
		return LocalDateTime.ofEpochSecond((time+"").length() > 10 ? time / SECOND_MILLIS : time, 0, ZoneOffset.ofHours(8));
	}

	/**
	 * 传入格式，返回字符串类型的时间
	 * @param format
	 * @return
	 */
	public static String now(String format) {
		return formatLocalDateTimeToStr(completeToFullDateTime(format), now());
	}

	public static String completeToFullDateTime(String input) {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException("Input cannot be null or empty");
		}

		String trimmedInput = input.trim();

		// 检查是否已经是完整格式
		if (trimmedInput.length() == 19) {
			return trimmedInput;
		}

		if (trimmedInput.length() == 16) {
			return trimmedInput + ":00";
		}

		if (trimmedInput.length() >= 13 && trimmedInput.length() <= 14) {
			return trimmedInput.trim() + ":00:00";
		}
		return input;
	}

	/**
	 * 获取指定日期指定格式字符串
	 *
	 * @param dateTime
	 * @param DATE_FORMAT
	 */
	public static String appointedDayStrToFormatStr(String dateTime, String STR_DATE_FORMAT, String DATE_FORMAT) {
		LocalDateTime time = parse(dateTime, STR_DATE_FORMAT);
		return format(time, DATE_FORMAT);
	}

	/**
	 * compare two date String with a pattern
	 *
	 * @param date1
	 * @param date2
	 * @param pattern
	 * @return
	 */
	public static int compareDate(String date1, String date2, String pattern) {
		long time1 = getTime(parse(date1, pattern));
		long time2 = getTime(parse(date2, pattern));
		if (time1 > time2) {
			return 1;
		} else if (time1 < time2) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 是否今天
	 *
	 * @param date 日期
	 * @return 是否
	 */
	public static boolean isToday(LocalDateTime date) {
		return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
	}

	/**
	 * 是否昨天
	 *
	 * @param date 日期
	 * @return 是否
	 */
	public static boolean isYesterday(LocalDateTime date) {
		return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now().minusDays(1));
	}

	public static LocalDateTime addTime(Duration duration) {
		return LocalDateTime.now().plus(duration);
	}

	/**
	 * 时间戳转日期
	 * @param time
	 * @return String
	 */
	public static String timestampToDate(Long time) {
		return parse(timestampToLocalDateTime(time), DEFAULT_DATE_TIME_FORMAT);
	}

	/**
	 * num 分钟后才能执行
	 * @param now
	 * @param num
	 */
	public static boolean afterWithMinute(LocalDateTime now, int num) {
		if (ObjectUtil.isNotNull(now) && TimeUtils.now().compareTo(now.plusMinutes(num)) > 0){
			return true;
		}
		return false;
	}

	/**
	 * 返回字符串类型的10位时间戳
	 * @return
	 */
	public static String getTimestamp() {
		return String.valueOf(System.currentTimeMillis() / 1000L);
	}

	/**
	 * YYYY_MM_DD_HH_MM_SS
	 * 日期型字符串转化为日期 格式
	 */
	public static LocalDateTime parseLocalDateTime(String str) {
		if (str == null) {
			return null;
		}
		try {
			return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
		} catch (Exception e) {
			return now();
		}
	}

	/**
	 * 获取LocalDateTime的时间戳
	 *
	 * @return: 13位的时间戳
	 */
	public static long getTimestamp(String now) {
		return getTime(parseLocalDateTime(now));
	}

	/**
	 * convert a date to string in a specifies fromat.
	 *
	 * @param date
	 * @param DATE_FORMAT
	 * @return
	 */
	public static String dateToStr(LocalDateTime date, String DATE_FORMAT) {
		if (date == null) {
			return null;
		}
		return format(date, DATE_FORMAT);
	}

	/**
	 * 获取 RFC 3339 格式字符串时间
	 * 例如：2023-09-11T16:29:35.120+08:00
	 * @return LocalDateTime() 当前日期
	 */
	public static String nowRFC3339() {
		return nowRFC3339(now());
	}

	public static String nowRFC3339(LocalDateTime now) {
		OffsetDateTime offsetDateTime = now.atOffset(ZoneOffset.ofHours(8));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		return offsetDateTime.format(formatter);
	}

	public static long getEndOfDay() {
		Calendar calendar = Calendar.getInstance();

		// 设置时间为当天的23:59:59.999
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTimeInMillis();
	}
}
