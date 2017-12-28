package com.qiuyj.excel.dataconverter.date.pattern;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiuyj
 * @since 2017/12/28
 */
public class JavaUtilDatePattern implements DatePattern<Date> {
  private static final Map<String, DateFormat> CACHED_DATE_FORMAT;
  static {
    CACHED_DATE_FORMAT = new HashMap<>();
    CACHED_DATE_FORMAT.put("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd"));
    CACHED_DATE_FORMAT.put("yyyy-MM-dd HH:mm:ss", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
  }

  private static final JavaUtilDatePattern INSTANCE = new JavaUtilDatePattern();

  private JavaUtilDatePattern() {}

  public static JavaUtilDatePattern getInstance() {
    return INSTANCE;
  }

  @Override
  public String format(Date date, String pattern) {
    DateFormat df = (DateFormat) lookupPattern(pattern);
    return df.format(date);
  }

  @Override
  public Date parse(String dateStr, String pattern) {
    DateFormat df = (DateFormat) lookupPattern(pattern);
    try {
      return df.parse(dateStr);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Error pattern: '" + dateStr + "'");
    }
  }

  @Override
  public Map getContainer() {
    return CACHED_DATE_FORMAT;
  }

  @Override
  public Object toFormatter(String pattern) {
    return new SimpleDateFormat(pattern);
  }
}