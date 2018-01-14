package com.qiuyj.commons.bean.propertyconverter;

import com.qiuyj.commons.bean.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class IntegerPropertyConverter implements PropertyConverter {

  private final boolean primitive;

  public IntegerPropertyConverter(boolean primitive) {
    this.primitive = primitive;
  }

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    if (primitive) {
      return Integer.parseInt(strValue);
    }
    else {
      return Integer.valueOf(strValue);
    }
  }

  @Override
  public String asString(Object value) {
    if (primitive) {
      return String.valueOf((int) value);
    }
    else {
      return value.toString();
    }
  }
}