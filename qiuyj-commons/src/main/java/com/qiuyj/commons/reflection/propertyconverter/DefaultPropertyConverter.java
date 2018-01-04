package com.qiuyj.commons.reflection.propertyconverter;

import com.qiuyj.commons.reflection.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class DefaultPropertyConverter implements PropertyConverter {

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    return strValue;
  }
}
