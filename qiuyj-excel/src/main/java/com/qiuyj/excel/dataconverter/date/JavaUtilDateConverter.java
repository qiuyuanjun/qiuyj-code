package com.qiuyj.excel.dataconverter.date;

import com.qiuyj.excel.dataconverter.date.AbstractDateConverter;
import com.qiuyj.excel.dataconverter.pattern.DatePattern;
import com.qiuyj.excel.dataconverter.pattern.JavaUtilDatePattern;

import java.util.Date;

/**
 * @author qiuyj
 * @since 2017/12/28
 */
public class JavaUtilDateConverter extends AbstractDateConverter<Date> {

  @Override
  protected DatePattern<Date> lookupPattern() {
    return JavaUtilDatePattern.getInstance();
  }
}