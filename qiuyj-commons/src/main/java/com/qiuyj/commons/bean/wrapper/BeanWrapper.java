package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.ConfigurablePropertyAccessor;

import java.beans.PropertyDescriptor;

/**
 * @author qiuyj
 * @since 2018/1/3
 */
public interface BeanWrapper<T> extends ConfigurablePropertyAccessor, WrappedObjectInfo<T> {

  /**
   * 得到所有的属性描述器
   */
  PropertyDescriptor[] getPropertyDescriptors();

  /**
   * 得到某一个属性对应的属性描述器
   */
  PropertyDescriptor getPropertyDescriptor(String property);
}