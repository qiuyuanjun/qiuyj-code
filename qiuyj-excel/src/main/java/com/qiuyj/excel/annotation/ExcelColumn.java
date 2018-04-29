package com.qiuyj.excel.annotation;

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
  
}