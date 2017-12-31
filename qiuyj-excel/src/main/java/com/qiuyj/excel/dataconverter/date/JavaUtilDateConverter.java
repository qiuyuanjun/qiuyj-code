package com.qiuyj.excel.dataconverter.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 主要用于java.util.Date日期类的转换
 * @author qiuyj
 * @since 2017/12/28
 */
@SuppressWarnings("unchecked")
public class JavaUtilDateConverter extends AbstractDateConverter<Date> {
  private static final Map CACHED_DATE_FORMAT;
  static {
    CACHED_DATE_FORMAT = new HashMap();
    CACHED_DATE_FORMAT.put("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd"));
    CACHED_DATE_FORMAT.put("yyyy-MM-dd HH:mm:ss", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    CACHED_DATE_FORMAT.put("yyyy年MM月dd日", new SimpleDateFormat("yyyy年MM月dd日"));
  }

  @Override
  protected Map getPatternContainer() {
    return CACHED_DATE_FORMAT;
  }

  @Override
  protected Object toFormatter(String pattern) {
    return new SimpleDateFormat(pattern);
  }

  @Override
  public String convertData(Date data, String pattern) {
    DateFormat df = (DateFormat) lookupPattern(pattern);
    return df.format(data);
  }

  @Override
  public Date convertString(String str, String pattern) {
    DateFormat df = (DateFormat) lookupPattern(pattern);
    try {
      return df.parse(str);
    }
    catch (ParseException e) {
      throw new IllegalArgumentException("Error date pattern: '" + pattern + "'");
    }
  }
}