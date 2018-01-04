package com.qiuyj.commons.reflection;

import com.qiuyj.commons.reflection.propertyconverter.*;

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

    /* integer */
    registerDefaultPropertyConverter(Integer.class, new IntegerPropertyConverter(false));
    registerDefaultPropertyConverter(int.class, new IntegerPropertyConverter(true));

    /* long */
    registerDefaultPropertyConverter(Long.class, new LongPropertyConverter(false));
    registerDefaultPropertyConverter(long.class, new LongPropertyConverter(true));

    /* float */
    registerDefaultPropertyConverter(Float.class, new FloatPropertyConverter(false));
    registerDefaultPropertyConverter(float.class, new FloatPropertyConverter(true));

    /* double */
    registerDefaultPropertyConverter(Double.class, new DoublePropertyConverter(false));
    registerDefaultPropertyConverter(double.class, new DoublePropertyConverter(true));

    /* boolean */
    registerDefaultPropertyConverter(Boolean.class, new BooleanPropertyConverter(false));
    registerDefaultPropertyConverter(boolean.class, new BooleanPropertyConverter(true));

    /* class */
    registerDefaultPropertyConverter(Class.class, new ClassPropertyConverter());

    /* array */
    registerDefaultPropertyConverter(Integer[].class, new ArrayPropertyConverter(Integer.class));
    registerDefaultPropertyConverter(int[].class, new ArrayPropertyConverter(int.class));
    registerDefaultPropertyConverter(Long[].class, new ArrayPropertyConverter(Long.class));
    registerDefaultPropertyConverter(long[].class, new ArrayPropertyConverter(long.class));
    registerDefaultPropertyConverter(Float[].class, new ArrayPropertyConverter(Float.class));
    registerDefaultPropertyConverter(float[].class, new ArrayPropertyConverter(float.class));
    registerDefaultPropertyConverter(Double[].class, new ArrayPropertyConverter(Double.class));
    registerDefaultPropertyConverter(double[].class, new ArrayPropertyConverter(double.class));
  }

  static void registerDefaultPropertyConverter(Class<?> cls, PropertyConverter converter) {
    DEFAULT_PROPERTY_CONVERTERS.put(cls, PropertyConverter.getProxy(converter));
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

  public static PropertyConverter getDefaultPropertyConverter(Class<?> cls) {
    Objects.requireNonNull(cls);
    return DEFAULT_PROPERTY_CONVERTERS.get(cls);
  }
}