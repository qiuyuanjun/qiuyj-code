package com.qiuyj.commons.reflection;

/**
 * @author qiuyj
 * @since 2018/1/3
 */
public interface ObjectWrapper<T> extends PropertyAccessor {

  /**
   * 得到被包装的对象
   */
  T getWrappedObject();

  /**
   * 设置属性值
   * @param property 属性
   * @param value 值
   */
  void set(String property, Object value);

  /**
   * 得到对应的属性值
   * @param property 属性
   */
  Object get(String property);
}