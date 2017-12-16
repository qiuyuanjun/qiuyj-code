package com.qiuyj.tools.mybatis.annotation;

import org.apache.ibatis.type.JdbcType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiuyj
 * @since 2017/12/14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Enumerated {

  /**
   * 代表当前枚举采用的是name还是ordinal作为数据库的值
   * 默认是采用ordinal作为数据库的值
   */
  ValueType type() default ValueType.ORDINAL;

  JdbcType jdbcType() default JdbcType.TINYINT;

  enum ValueType {
    STRING, ORDINAL
  }
}
