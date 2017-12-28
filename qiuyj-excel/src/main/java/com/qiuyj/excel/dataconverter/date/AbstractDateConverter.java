package com.qiuyj.excel.dataconverter.date;

import com.qiuyj.excel.dataconverter.DataConverter;
import com.qiuyj.excel.dataconverter.pattern.DatePattern;

/**
 * @author qiuyj
 * @since 2017/12/28
 */
public abstract class AbstractDateConverter<T> implements DataConverter<T> {

  @Override
  public String convertData(T data, String pattern) {
    return lookupPattern().format(data, pattern);
  }

  @Override
  public T convertString(String str, String pattern) {
    return lookupPattern().parse(str, pattern);
  }

  protected abstract DatePattern<T> lookupPattern();
}
