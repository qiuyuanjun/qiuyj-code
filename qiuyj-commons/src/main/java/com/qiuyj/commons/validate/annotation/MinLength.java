package com.qiuyj.commons.validate.annotation;

import com.qiuyj.commons.validate.ValidatorBaseAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiuyj
 * @since 2018-05-30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ValidatorBaseAnnotation
public @interface MinLength {

  /**
   * 最小长度，默认6
   */
  int value() default 6;

  String message() default "";
}
