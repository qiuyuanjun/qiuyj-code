package com.qiuyj.commons.validate.annotation;

/**
 * @author qiuyj
 * @since 2018-06-01
 */
@FunctionalInterface
public interface AnnotationInstanceCapable {

  /**
   * 得到对应的注解对象
   */
  AnnotationInstance getAnnotationInstance();
}
