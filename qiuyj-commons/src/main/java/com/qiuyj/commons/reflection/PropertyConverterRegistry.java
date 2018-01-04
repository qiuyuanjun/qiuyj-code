package com.qiuyj.commons.reflection;

import com.qiuyj.commons.reflection.propertyconverter.DefaultPropertyConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public final class PropertyConverterRegistry {

  private static final Map<Class<?>, PropertyConverter> DEFAULT_PROPERTY_CONVERTERS;

  static {
    DEFAULT_PROPERTY_CONVERTERS = new HashMap<>();

  }

  private static final PropertyConverter DEFAULT_CONVERTER
      = PropertyConverter.getProxy(new DefaultPropertyConverter());

  private Map<Class<?>, PropertyConverter> customerPropertyConverters;

  public PropertyConverter getPropertyConverter(Class<?> cls) {
    Objects.requireNonNull(cls);
    PropertyConverter converter = DEFAULT_PROPERTY_CONVERTERS.get(cls);
    if (Objects.isNull(converter)) {
      if (Objects.isNull(customerPropertyConverters)) {
        customerPropertyConverters = new HashMap<>();
      }
      else {
        converter = customerPropertyConverters.get(cls);
      }
      if (Objects.isNull(converter)) {
        converter = DEFAULT_CONVERTER;
      }
    }
    return converter;
  }

  public void registerCustomerPropertyConverter(Class<?> cls, PropertyConverter converter) {
    if (Objects.isNull(cls) || Objects.isNull(converter)) {
      throw new NullPointerException();
    }
    else if (Objects.isNull(customerPropertyConverters)) {
      customerPropertyConverters = new HashMap<>();
    }
    customerPropertyConverters.put(cls, PropertyConverter.getProxy(converter));
  }

  public void registerCustomerPropertyConverters(Map<Class<?>, PropertyConverter> converters) {
    Objects.requireNonNull(converters);
    if (converters.size() > 0) {
      converters.forEach(this::registerCustomerPropertyConverter);
    }
  }

  public Map<Class<?>, PropertyConverter> getCustomerPropertyConverters() {
    return customerPropertyConverters;
  }
}