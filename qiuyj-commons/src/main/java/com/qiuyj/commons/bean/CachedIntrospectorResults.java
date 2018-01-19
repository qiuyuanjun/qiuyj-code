package com.qiuyj.commons.bean;

import com.qiuyj.commons.StringUtils;
import com.qiuyj.commons.bean.exception.ReflectionException;
import com.qiuyj.commons.cache.Cache;
import com.qiuyj.commons.cache.impl.SoftReferenceCache;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author qiuyj
 * @since 2018/1/19
 */
public class CachedIntrospectorResults {

  private static final Cache<Class<?>, BeanInfo> BEAN_INFO_CACHE = new SoftReferenceCache<>();

  private BeanInfo currBeanInfo;

  public CachedIntrospectorResults(Class<?> cls) {
    Objects.requireNonNull(cls);
    currBeanInfo = BEAN_INFO_CACHE.getValue(cls);
    if (Objects.isNull(currBeanInfo)) {
      try {
        currBeanInfo = Introspector.getBeanInfo(cls);
      } catch (IntrospectionException e) {
        throw new ReflectionException("Error getting bean info of class: " + cls + ".\n Caused by: " + e, e);
      }
    }
  }

  public PropertyDescriptor getPropertyDescriptor(String property) {
    if (StringUtils.isBlank(property)) {
      throw new IllegalArgumentException("Parameter 'property' can not be null or empty");
    }
    else {
      return Optional.ofNullable(currBeanInfo.getPropertyDescriptors())
          .map(Arrays::stream)
          .flatMap(pdStream ->
              pdStream.filter(pd -> pd.getName().equals(property))
                  .findFirst())
          .orElse(null);
    }
  }

  public BeanInfo getBeanInfo() {
    return currBeanInfo;
  }
}