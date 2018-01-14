package com.qiuyj.commons.bean.propertyconverter;

import com.qiuyj.commons.ClassUtils;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class ClassPropertyConverter implements PropertyConverter {

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    return ClassUtils.resolveClassName(strValue, null);
  }

  @Override
  public String asString(Object value) {
    if (value instanceof Class) {
      return ((Class<?>) value).getName();
    }
    else {
      throw new IllegalStateException("Not a Class object");
    }
  }
}
