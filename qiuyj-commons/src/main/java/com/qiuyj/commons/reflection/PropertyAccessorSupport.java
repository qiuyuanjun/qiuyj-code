package com.qiuyj.commons.reflection;

import com.qiuyj.commons.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public abstract class PropertyAccessorSupport implements ConfigurablePropertyAccessor {

  /**
   * 如果对应的值是null，那么就会获得这个对象
   */
  private static final Object NULL_VALUE = new Object();

  /**
   * 是否支持字符串类型自动转换，默认支持
   */
  private boolean convertIfIsStringValue;

  private final PropertyConverterRegistry propertyConverterRegistry;

  /**
   * 属性值的缓存
   */
  private final Map<String, Object> propertyValues;

  public PropertyAccessorSupport() {
    convertIfIsStringValue = true;
    propertyConverterRegistry = new PropertyConverterRegistry();
    propertyValues = new HashMap<>();
  }

  @Override
  public void setConvertIfIsStringValue(boolean convertIfIsStringValue) {
    this.convertIfIsStringValue = convertIfIsStringValue;
  }

  @Override
  public void setCustomizedPropertyConverter(Class<?> cls, PropertyConverter converter) {
    propertyConverterRegistry.registerCustomerPropertyConverter(cls, converter);
  }

  @Override
  public void setAutoInstantiateNestedPropertyNullValue(boolean autoInstantiateNestedPropertyNullValue) {
    throw new UnsupportedOperationException("Unsupport nested property");
  }

  @Override
  public void setProperty(String property, Object value) {
    property = resolvePropertyName(property);
    Object maskValue = maskValue(value);
    doSetProperty(property, value);
    propertyValues.put(property, maskValue);
  }

  protected abstract void doSetProperty(String property, Object value);

  private static Object maskValue(Object value) {
    return Optional.ofNullable(value).orElse(NULL_VALUE);
  }

  @Override
  public void setProperty(String property, String strValue) {
    if (!convertIfIsStringValue) {
      throw new IllegalStateException("String conversion injection is not currently supported");
    }
    else {
      Object value = null;
      if (Objects.nonNull(strValue)) {
        Class<?> propertyType = getPropertyType(property);
        PropertyConverter converter = propertyConverterRegistry.getPropertyConverter(propertyType);
        value = converter.getConvertedPropertyValue(strValue);
      }
      setProperty(property, value);
    }
  }

  @Override
  public Object getProperty(String property) {
    property = resolvePropertyName(property);
    Object value = unmaskValue(propertyValues.get(property));
    if (Objects.isNull(value)) {
      // 自定义查找规则，子类实现
      value = doGetProperty(property);
    }
    return value;
  }

  protected abstract Object doGetProperty(String property);

  @Override
  public String getPropertyAsString(String property) {
    Object unmaskValue = unmaskValue(getProperty(property));
    if (Objects.nonNull(unmaskValue)) {
      Class<?> propertyType = getPropertyType(property);
      PropertyConverter pc = propertyConverterRegistry.getPropertyConverter(propertyType);
      unmaskValue = pc.asString(unmaskValue);
    }
    return (String) unmaskValue;
  }

  private static Object unmaskValue(Object value) {
    return value == NULL_VALUE ? null : value;
  }

  protected String resolvePropertyName(String propertyName) {
    if (StringUtils.isBlank(propertyName)) {
      throw new IllegalArgumentException("Property name can not be null");
    }
    return propertyName;
  }

  /**
   * 得到对应的属性的Class类型，有相应的子类去实现
   * @param property 属性名
   * @return 对应的Class对象
   */
  protected abstract Class<?> getPropertyType(String property);
}