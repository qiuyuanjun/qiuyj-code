package com.qiuyj.commons.bean;

import java.util.Map;

/**
 * @author qiuyj
 * @since 2018/1/18
 */
public interface PropertyAccessor {

  char NESTED_PROPERTY_SEPARATOR = '.';

  char INDEXED_PROPERTY_PREFIX = '[';

  char INDEXED_PROPERTY_SUFFIX = ']';

  void setPropertyValue(String property, Object value);

  Object getPropertyValue(String property);

  void setPropertyValues(Map<String, Object> propertyValues);

  String getPropertyValueAsString(String property);

  void convertAndSetPropertyValueString(String property, String valueStr);

  Object getWrappedInstance();

  Class<?> getWrappedClass();
}