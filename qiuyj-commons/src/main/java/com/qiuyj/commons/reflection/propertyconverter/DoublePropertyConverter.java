package com.qiuyj.commons.reflection.propertyconverter;

import com.qiuyj.commons.reflection.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class DoublePropertyConverter implements PropertyConverter {

  private final boolean primitive;

  public DoublePropertyConverter(boolean primitive) {
    this.primitive = primitive;
  }

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    if (primitive) {
      return Double.parseDouble(strValue);
    }
    else {
      return Double.valueOf(strValue);
    }
  }

  @Override
  public String asString(Object value) {
    if (primitive) {
      return String.valueOf((double) value);
    }
    else {
      return value.toString();
    }
  }
}