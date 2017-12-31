package com.qiuyj.excel.dataconverter.date;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 主要用于jsr310规范下的时间日期api的转换
 * @author qiuyj
 * @since 2017/12/28
 */
@SuppressWarnings("unchecked")
public class Jsr310DateConverter extends AbstractDateConverter<TemporalAccessor> {
  private static final Map CACHED_JSR310_FORMATTER;
  static {
    CACHED_JSR310_FORMATTER = new HashMap();
    CACHED_JSR310_FORMATTER.put("yyyy-MM-dd", DateTimeFormatter.ISO_DATE);
    CACHED_JSR310_FORMATTER.put("yyyy-MM-dd HH:mm:ss", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    CACHED_JSR310_FORMATTER.put("yyyy年MM月dd日", DateTimeFormatter.ofPattern("yyyy年MM月dd日", Locale.CHINA));
  }

  @Override
  protected Map getPatternContainer() {
    return CACHED_JSR310_FORMATTER;
  }

  @Override
  protected Object toFormatter(String pattern) {
    return DateTimeFormatter.ofPattern(pattern);
  }

  @Override
  public String convertData(TemporalAccessor data, String pattern) {
    DateTimeFormatter dtf = (DateTimeFormatter) lookupPattern(pattern);
    return dtf.format(data);
  }

  @Override
  public TemporalAccessor convertString(String str, String pattern) {
    DateTimeFormatter dtf = (DateTimeFormatter) lookupPattern(pattern);
    return dtf.parse(str);
  }
}
