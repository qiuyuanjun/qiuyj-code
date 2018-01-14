package com.qiuyj.commons.bean.propertyconverter;

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
