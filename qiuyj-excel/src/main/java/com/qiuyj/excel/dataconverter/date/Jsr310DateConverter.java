package com.qiuyj.excel.dataconverter.date;

import com.qiuyj.excel.dataconverter.pattern.DatePattern;
import com.qiuyj.excel.dataconverter.pattern.Jsr310DateTimeDatePattern;

import java.time.temporal.TemporalAccessor;

/**
 * @author qiuyj
 * @since 2017/12/28
 */
public class Jsr310DateConverter extends AbstractDateConverter<TemporalAccessor> {

  @Override
  protected DatePattern<TemporalAccessor> lookupPattern() {
    return Jsr310DateTimeDatePattern.getInstance();
  }
}
