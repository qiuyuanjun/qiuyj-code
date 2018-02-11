package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.ConfigurablePropertyAccessor;

import java.beans.PropertyDescriptor;

/**
 * @author qiuyj
 * @since 2018/1/19
 */
public interface BeanWrapper extends ConfigurablePropertyAccessor {

  PropertyDescriptor getPropertyDescriptor(String property);

  PropertyDescriptor[] getPropertyDescriptors();

  /**
   * 设置当如果一个属性没有对应的getterSetter方法的时候，是否支持直接通过Field操作
   * 如果为false，那么当属性没有getterSetter方法的时候，抛出异常
   */
  void setFieldOperationSupport(boolean fieldOperationSupport);
}