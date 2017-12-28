package com.qiuyj.excel.annotation;

/**
 * 用于导出excel，标识对应的javabean的属性和excel一列对应
 * @author qiuyj
 * @since 2017/12/28
 */
public @interface ExcelColumn {

  /**
   * excel列名
   */
  String name() default "";
}