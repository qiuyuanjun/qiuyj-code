package com.qiuyj.commons.bean.propertyconverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class LongPropertyConverter implements PropertyConverter {

  private final boolean primitive;

  public LongPropertyConverter(boolean primitive) {
    this.primitive = primitive;
  }

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    if (primitive) {
      return Long.parseLong(strValue);
    }
    else {
      return Long.valueOf(strValue);
    }
  }

  @Override
  public String asString(Object value) {
    if (primitive) {
      return String.valueOf((long) value);
    }
    else {
      return value.toString();
    }
  }
}
