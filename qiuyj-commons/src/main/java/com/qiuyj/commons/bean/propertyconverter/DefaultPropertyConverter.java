package com.qiuyj.commons.bean.propertyconverter;

import com.qiuyj.commons.bean.PropertyConverter;

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
