package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.ReflectionException;
import com.qiuyj.commons.cache.impl.SoftReferenceCache;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/7
 */
public class CachedIntrospectResult {

  private static final SoftReferenceCache<Class<?>, BeanInfo> BEAN_INFO_CACHE
      = new SoftReferenceCache<>();

  private final Class<?> beanCls;

  private BeanInfo beanInfo;

  public CachedIntrospectResult(Class<?> beanCls) {
    this.beanCls = beanCls;
    beanInfo = BEAN_INFO_CACHE.getValue(beanCls);
    if (Objects.isNull(beanInfo)) {
      try {
        beanInfo = Introspector.getBeanInfo(beanCls);
      } catch (IntrospectionException e) {
        throw new ReflectionException("Error getting bean info of class: " + beanCls + ".\n Caused by: " + e, e);
      }
      BEAN_INFO_CACHE.setValue(beanCls, beanInfo);
    }
  }

  public BeanInfo getBeanInfo() {
    return beanInfo;
  }

  public PropertyDescriptor getPropertyDescriptor(String property) {
    for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
      if (pd.getName().equals(property)) {
        return pd;
      }
    }
    return null;
  }
}