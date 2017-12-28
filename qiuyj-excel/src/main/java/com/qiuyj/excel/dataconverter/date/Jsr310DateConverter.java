package com.qiuyj.excel.dataconverter.date;

import com.qiuyj.excel.dataconverter.date.pattern.DatePattern;
import com.qiuyj.excel.dataconverter.date.pattern.Jsr310DateTimeDatePattern;

import java.time.temporal.TemporalAccessor;

/**
 * 主要用于jsr310规范下的时间日期api的转换
 * @author qiuyj
 * @since 2017/12/28
 */
public class Jsr310DateConverter extends AbstractDateConverter<TemporalAccessor> {

  @Override
  protected DatePattern<TemporalAccessor> lookupPattern() {
    return Jsr310DateTimeDatePattern.getInstance();
  }
}
