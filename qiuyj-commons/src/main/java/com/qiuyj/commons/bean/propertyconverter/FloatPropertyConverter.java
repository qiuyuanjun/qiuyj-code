package com.qiuyj.commons.bean.propertyconverter;

import com.qiuyj.commons.bean.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class FloatPropertyConverter implements PropertyConverter {

  private final boolean primitive;

  public FloatPropertyConverter(boolean primitive) {
    this.primitive = primitive;
  }

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    if (primitive) {
      return Float.parseFloat(strValue);
    }
    else {
      return Float.valueOf(strValue);
    }
  }

  @Override
  public String asString(Object value) {
    if (primitive) {
      return String.valueOf((float) value);
    }
    else {
      return value.toString();
    }
  }
}
