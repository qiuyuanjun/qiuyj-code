package com.qiuyj.commons.bean.propertyconverter;

import com.qiuyj.commons.bean.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class BooleanPropertyConverter implements PropertyConverter {

  private final boolean primitive;

  public BooleanPropertyConverter(boolean primitive) {
    this.primitive = primitive;
  }

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    if (primitive) {
      return Boolean.parseBoolean(strValue);
    }
    else {
      return Boolean.valueOf(strValue);
    }
  }

  @Override
  public String asString(Object value) {
    if (primitive) {
      return String.valueOf((boolean) value);
    }
    else {
      return value.toString();
    }
  }
}