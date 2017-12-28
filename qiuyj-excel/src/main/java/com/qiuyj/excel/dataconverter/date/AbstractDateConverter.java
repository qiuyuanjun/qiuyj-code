package com.qiuyj.excel.dataconverter.date;

import com.qiuyj.excel.dataconverter.DataConverter;

import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/28
 */
public abstract class AbstractDateConverter<T> implements DataConverter<T> {

  /**
   * 得到缓存所有格式转换器的容器
   */
  protected abstract Map getPatternContainer();

  /**
   * 将对应的格式转换成日期格式转换器
   */
  protected abstract Object toFormatter(String pattern);

  /**
   * 根据给定的格式查找对应的日期转换器，如果默认的没有，那么需要创建一个
   * 然后再加入到默认的缓存里面（这里需要同步处理）
   */
  @SuppressWarnings("unchecked")
  protected Object lookupPattern(String pattern) {
    Objects.requireNonNull(pattern, "Date pattern can not be null");
    Object formatter = getPatternContainer().get(pattern);
    if (Objects.isNull(formatter)) {
      synchronized (getPatternContainer()) {
        formatter = getPatternContainer().get(pattern);
        if (Objects.isNull(formatter)) {
          formatter = toFormatter(pattern);
          getPatternContainer().put(pattern, formatter);
        }
      }
    }
    return formatter;
  }
}