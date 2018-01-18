package com.qiuyj.commons.bean;

import com.qiuyj.commons.bean.exception.ReflectionException;
import com.qiuyj.commons.bean.propertyconverter.PropertyConverter;
import com.qiuyj.commons.bean.propertyconverter.PropertyConverterRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author qiuyj
 * @since 2018/1/18
 */
public abstract class PropertyAccessorSupport implements ConfigurablePropertyAccessor {

  private boolean convertIfIsStringValue = true;

  private final PropertyConverterRegistry propertyConverterRegistry = new PropertyConverterRegistry();

  @Override
  public void setConvertIfIsStringValue(boolean convertIfIsStringValue) {
    this.convertIfIsStringValue = convertIfIsStringValue;
  }

  public boolean isConvertIfIsStringValue() {
    return convertIfIsStringValue;
  }

  @Override
  public void registCustomizedPropertyConverter(Class<?> cls, PropertyConverter propertyConverter) {
    propertyConverterRegistry.registerCustomerPropertyConverter(cls, propertyConverter);
  }

  @Override
  public void setPropertyValues(Map<String, Object> propertyValues) {
    Objects.requireNonNull(propertyValues).forEach(this::setPropertyValue);
  }

  @Override
  public String getPropertyValueAsString(String property) {
    if (!isConvertIfIsStringValue()) {
      throw new ReflectionException("String conversion is not currently supported");
    }
    else {
      return Optional.ofNullable(getPropertyValue(property))
          .map(propertyConverterRegistry.getPropertyConverter(getPropertyType(property))::asString)
          .orElse(null);
    }
  }

  @Override
  public void convertAndSetPropertyValueString(String property, String valueStr) {
    Object value = Optional.ofNullable(valueStr)
        .map(propertyConverterRegistry.getPropertyConverter(getPropertyType(property))::getConvertedPropertyValue)
        .orElse(null);
    setPropertyValue(property, value);
  }

  /**
   * 得到对应的属性的Class对象，如果属性不存在，那么抛出异常
   * @param property 属性名
   * @return 对应的属性的Class对象
   */
  protected abstract Class<?> getPropertyType(String property);
}