package com.qiuyj.classreading;

import com.qiuyj.commons.AnnotationAttributes;

/**
 * @author qiuyj
 * @since 2018/3/3
 */
public interface Metadata {

  /**
   * 得到当前元信息所代表的Class，Field，Method或者Annotation的名称（全名）
   */
  String getName();

  /**
   * 判断当前元信息所代表的Class，Field，Method或者Annotation是否被所指定的注解标注
   * @param annotationName 注解名称
   * @return true代表被指定的注解标注，false表示没有被指定的注解标注
   */
  boolean isAnnotated(String annotationName);

  /**
   * 得到当前元信息所代表的Class，Field，Method或者Annotation所标注的指定的注解的所有属性
   * 如果当前元信息没有标注给定的注解，那么返回null
   * @param annotationName 标注的注解名
   * @return AnnotationAttributes对象或者null
   */
  AnnotationAttributes annotationAttributesFor(String annotationName);

  /**
   * 得到当前元信息所标注的所有有效的注解名称，不包括java.lang.annotation下的注解
   * @return 如果当前元信息没有标注任何有效的注解，那么返回一个空数组，如果有，那么返回所有有效的注解的名称的数组
   */
  String[] getAnnotationNames();
}