package com.qiuyj.commons.bean;

import com.qiuyj.commons.StringUtils;
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

  private static final Cache<Class<?>, CachedIntrospectorResults> BEAN_INTROSPECTOR_RESULTS = new SoftReferenceCache<>();

  private BeanInfo currBeanInfo;

  private CachedIntrospectorResults(BeanInfo beanInfo) {
    currBeanInfo = beanInfo;
  }

  public static CachedIntrospectorResults forClass(Class<?> cls) {
    CachedIntrospectorResults rs = BEAN_INTROSPECTOR_RESULTS.getValue(cls);
    if (Objects.isNull(rs)) {
      BeanInfo beanInfo;
      try {
        beanInfo = Introspector.getBeanInfo(cls);
      } catch (IntrospectionException e) {
        throw new IllegalStateException("Error getting bean info of class: " + cls.getName() + ".\n Caused by: " + e, e);
      }
      rs = new CachedIntrospectorResults(beanInfo);
      BEAN_INTROSPECTOR_RESULTS.setValue(cls, rs);
    }
    return rs;
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