package com.qiuyj.commons.reflection.propertyconverter;

import com.qiuyj.commons.StringUtils;
import com.qiuyj.commons.reflection.PropertyConverter;
import com.qiuyj.commons.reflection.PropertyConverterRegistry;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class ArrayPropertyConverter implements PropertyConverter {

  private static final String DEFAULT_ARRAY_SEPERATOR = ", \t";

  private final Class<?> componentType;

  private final String seperator;

  public ArrayPropertyConverter(Class<?> componentType) {
    this(componentType, DEFAULT_ARRAY_SEPERATOR);
  }

  public ArrayPropertyConverter(Class<?> componentType, String seperator) {
    this.componentType = componentType;
    this.seperator = seperator;
  }

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    PropertyConverter converter = getPropertyConverter();
    String[] contents = StringUtils.delimiteToStringArray(strValue, seperator);
    Object arr = Array.newInstance(componentType, contents.length);
    int idx = 0;
    for (String content : contents) {
      Array.set(arr, idx++, converter.getConvertedPropertyValue(content));
    }
    return arr;
  }

  @Override
  public String asString(Object value) {
    if (!value.getClass().isArray()) {
      throw new IllegalArgumentException("Not an array object");
    }
    else {
      PropertyConverter converter = getPropertyConverter();
      int len = Array.getLength(value);
      StringJoiner joiner = new StringJoiner(",");
      for (int i = 0; i < len; i++) {
        joiner.add(converter.asString(Array.get(value, i)));
      }
      return joiner.toString();
    }
  }

  private PropertyConverter getPropertyConverter() {
    PropertyConverter converter = PropertyConverterRegistry.getDefaultPropertyConverter(componentType);
    if (Objects.isNull(converter)) {
      throw new IllegalStateException("Unsupport type: " + componentType);
    }
    else {
      return converter;
    }
  }
}