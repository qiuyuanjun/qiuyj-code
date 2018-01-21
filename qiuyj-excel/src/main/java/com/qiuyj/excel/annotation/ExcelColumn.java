package com.qiuyj.excel.annotation;

import com.qiuyj.excel.dataconverter.DataConverter;
import com.qiuyj.excel.dataconverter.DefaultDataConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于导出excel，标识对应的javabean的属性和excel一列对应
 * @author qiuyj
 * @since 2017/12/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ExcelColumn {

  /**
   * excel列名
   */
  String name() default "";

  /**
   * 列的下标，也就是导出excel的时候，对应的javabean的属性对应列的先后顺序
   */
  int index() default -1;

  /**
   * 数据转换器
   */
  Class<? extends DataConverter> dataConverterClass() default DefaultDataConverter.class;

  /**
   * 数据转换的格式，如果有必要的话
   */
  String pattern() default DataConverter.NO_PATTERN;
}