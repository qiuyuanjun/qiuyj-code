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
public @interface MaxLength {

  /**
   * 最大长度，默认20
   */
  int value() default 20;

  String message() default "";
}
