package com.qiuyj.commons.validate.annotation;

import com.qiuyj.commons.validate.ValidatorBaseAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在表示日期的属性上，表示必须大于注解值指定的日期
 * @author qiuyj
 * @since 2018-06-05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ValidatorBaseAnnotation
public @interface AfterDate {

  /**
   * 根据pattern格式对应的日期字符串值
   */
  String value() default "";

  /**
   * 日期格式
   */
  String pattern() default "yyyy-MM-dd";

  /**
   * 验证错误时候的信息
   */
  String message() default "";

  /**
   * 是否允许为{@code null}，如果为{@code true}，那么被标注的属性如果值为{@code null}，依然验证通过
   */
  boolean nullable() default true;
}
