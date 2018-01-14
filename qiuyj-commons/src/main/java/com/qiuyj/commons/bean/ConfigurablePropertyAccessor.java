package com.qiuyj.commons.bean;

import com.qiuyj.commons.bean.propertyconverter.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public interface ConfigurablePropertyAccessor extends PropertyAccessor {

  /**
   * 如果设置属性的值字符串，是否允许根据属性的类型自动转换
   */
  void setConvertIfIsStringValue(boolean convertIfIsStringValue);

  /**
   * 设置自定义的属性转换器
   */
  void setCustomizedPropertyConverter(Class<?> cls, PropertyConverter converter);

  /**
   * 如果设置的属性是内嵌属性，并且内嵌属性为null的时候，是否给他实例化
   */
  void setAutoInstantiateNestedPropertyNullValue(boolean autoInstantiateNestedPropertyNullValue);
}