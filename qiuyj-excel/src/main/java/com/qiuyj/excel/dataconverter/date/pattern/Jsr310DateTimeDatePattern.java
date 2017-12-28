package com.qiuyj.excel.dataconverter.date.pattern;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiuyj
 * @since 2017/12/28
 */
public class Jsr310DateTimeDatePattern implements DatePattern<TemporalAccessor> {
  private static final Map<String, DateTimeFormatter> CACHED_DATE_TIME_FORMATTER;
  static {
    CACHED_DATE_TIME_FORMATTER = new HashMap<>();
    CACHED_DATE_TIME_FORMATTER.put("yyyy-MM-dd", DateTimeFormatter.ISO_DATE);
    CACHED_DATE_TIME_FORMATTER.put("yyyy-MM-dd HH:mm:ss", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  private static final Jsr310DateTimeDatePattern INSTANCE = new Jsr310DateTimeDatePattern();

  private Jsr310DateTimeDatePattern() {}

  public static Jsr310DateTimeDatePattern getInstance() {
    return INSTANCE;
  }

  @Override
  public String format(TemporalAccessor date, String pattern) {
    DateTimeFormatter dtf = (DateTimeFormatter) lookupPattern(pattern);
    return dtf.format(date);
  }

  @Override
  public TemporalAccessor parse(String dateStr, String pattern) {
    DateTimeFormatter dtf = (DateTimeFormatter) lookupPattern(pattern);
    return dtf.parse(dateStr);
  }

  @Override
  public Map getContainer() {
    return CACHED_DATE_TIME_FORMATTER;
  }

  @Override
  public Object toFormatter(String pattern) {
    return DateTimeFormatter.ofPattern(pattern);
  }
}
