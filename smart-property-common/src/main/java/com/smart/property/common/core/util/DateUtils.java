package com.smart.property.common.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 * <p>统一项目内的日期/时间格式与转换，避免业务层散落 SimpleDateFormat 与 LocalDateTime 互转样板。</p>
 *
 * @author zzz
 * @since 2026-07-31
 */
public final class DateUtils {

    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_TIME);

    /** 项目统一时区 */
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");

    private DateUtils() {
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_FORMATTER);
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime == null ? null : dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String text) {
        return text == null || text.isEmpty() ? null : LocalDateTime.parse(text, DATETIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String text, String pattern) {
        return text == null || text.isEmpty() ? null : LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseDate(String text) {
        return text == null || text.isEmpty() ? null : LocalDate.parse(text, DATE_FORMATTER);
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : LocalDateTime.of(date, LocalTime.MIN);
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : LocalDateTime.of(date, LocalTime.MAX);
    }

    /**
     * Date 与 LocalDateTime 互转（仅在跨模块边界如 MQ/OSS/Jackson 出现 Date 时使用）
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE);
    }

    public static Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null : Date.from(dateTime.atZone(DEFAULT_ZONE).toInstant());
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }
}
