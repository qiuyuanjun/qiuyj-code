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

  /**
   * 如果采用的是ordinal作为数据库的值，那么该属性表示ordinal代表那种数字
   * 可选值为TINYINT, SMALLINT, INT, BIGINT
   */
  JdbcType ordinalValueType() default JdbcType.TINYINT;

  enum ValueType {
    STRING, ORDINAL
  }
}
