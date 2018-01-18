package com.qiuyj.commons.bean;

import com.qiuyj.commons.bean.propertyconverter.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/18
 */
public interface ConfigurablePropertyAccessor extends PropertyAccessor {

  void setConvertIfIsStringValue(boolean convertIfIsStringValue);

  void registCustomizedPropertyConverter(Class<?> cls, PropertyConverter propertyConverter);

  void setAutoInstantiationNestedNullValue(boolean autoInstantiationNestedNullValue);
}