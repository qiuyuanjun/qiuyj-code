package com.qiuyj.commons.reflection;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public abstract class PropertyAccessorSupport implements ConfigurablePropertyAccessor {

  private boolean convertIfIsStringValue;

  private final PropertyConverterRegistry propertyConverterRegistry;

  public PropertyAccessorSupport() {
    convertIfIsStringValue = true;
    propertyConverterRegistry = new PropertyConverterRegistry();
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
    return null;
  }

  @Override
  public String getPropertyAsString(String property) {
    return null;
  }

  /**
   * 得到对应的属性的Class类型，有相应的子类去实现
   * @param property 属性名
   * @return 对应的Class对象
   */
  protected abstract Class<?> getPropertyType(String property);
}