package com.qiuyj.commons.reflection;

/**
 * 属性-值的包装对象
 * @author qiuyj
 * @since 2018/1/3
 */
public interface PropertyValue {

  /**
   * 得到包装的属性名称
   */
  String getProperty();

  /**
   * 得到包装的值
   */
  Object getValue();
}