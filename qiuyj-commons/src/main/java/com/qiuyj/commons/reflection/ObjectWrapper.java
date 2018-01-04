package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;

/**
 * @author qiuyj
 * @since 2018/1/3
 */
public interface ObjectWrapper<T> extends ConfigurablePropertyAccessor {

  /**
   * 得到被包装的对象
   */
  T getWrappedInstance();

  /**
   * 得到被包装的对象的Class对象
   */
  Class<T> getWrappedClass();

  /**
   * 得到所有的属性描述器
   */
  PropertyDescriptor[] getPropertyDescriptors();

  /**
   * 得到某一个属性对应的属性描述器
   */
  PropertyDescriptor getPropertyDescriptor(String property);
}