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
}