package com.qiuyj.tools.mybatis.checker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiuyj
 * @since 2017/11/20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Orderd {

  /**
   * 优先级，数字越大优先级越高
   * 默认优先级最高
   */
  int value() default Integer.MAX_VALUE;
}
