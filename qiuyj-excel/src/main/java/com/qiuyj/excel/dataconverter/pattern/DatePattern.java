package com.qiuyj.excel.dataconverter.pattern;

import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/28
 */
public interface DatePattern<T> {

  String format(T date, String pattern);

  T parse(String dateStr, String pattern);

  Map getContainer();

  Object toFormatter(String pattern);

  @SuppressWarnings("unchecked")
  default Object lookupPattern(String pattern) {
    Objects.requireNonNull(pattern, "Date pattern can not be null");
    Map container = getContainer();
    Object formatter = container.get(pattern);
    if (Objects.isNull(formatter)) {
      synchronized (container) {
        formatter = container.get(pattern);
        if (Objects.isNull(formatter)) {
          formatter = toFormatter(pattern);
          container.put(pattern, formatter);
        }
      }
    }
    return formatter;
  }
}