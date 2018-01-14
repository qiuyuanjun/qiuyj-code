package com.qiuyj.commons.bean;

import com.qiuyj.commons.StringUtils;
import com.qiuyj.commons.bean.propertyconverter.PropertyConverter;
import com.qiuyj.commons.bean.propertyconverter.PropertyConverterRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public abstract class PropertyAccessorSupport implements ConfigurablePropertyAccessor {

  /**
   * 如果一个值是null，那么将其转换成此对象，缓存中不存储null
   */
  public static final Object NULL_VALUE = new Object();

  private final PropertyConverterRegistry propertyConverterRegistry = new PropertyConverterRegistry();

  private boolean convertIfIsStringValue = true;

  /**
   * 属性值的缓存对象，如果一个值不为null，那么直接从这里获取即可
   */
  private final Map<String, Object> cachedPropertyValues = new HashMap<>();

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
    throw new UnsupportedOperationException();
  }

  @Override
  public void setProperty(String property, Object value) {
    String realPropertyName = resolvePropertyExpression(property);
    value = maskValue(value);
    if (doSetProperty(realPropertyName, value)) {
      cachedPropertyValues.put(realPropertyName, value);
    }
    else {
      cachedPropertyValues.remove(realPropertyName);
    }
  }

  protected abstract boolean doSetProperty(String realPropertyName, Object value);

  @Override
  public void setProperty(String property, String strValue) {
    if (!convertIfIsStringValue) {
      throw new ReflectionException("String conversion injection is not currently supported");
    }
    else {
      Object value = null;
      property = resolvePropertyExpression(property);
      if (Objects.nonNull(strValue)) {
        Class<?> propertyType = getPropertyValueType(property);
        PropertyConverter converter = propertyConverterRegistry.getPropertyConverter(propertyType);
        value = converter.getConvertedPropertyValue(strValue);
      }
      setProperty(property, value);
    }
  }

  @Override
  public Object getProperty(String property) {
    String realPropertyName = resolvePropertyExpression(property);
    Object value = unmaskValue(cachedPropertyValues.get(realPropertyName));
    if (Objects.isNull(value) || !realPropertyName.equals(property)) {
      // 子类自定义规则查找对应的属性值
      value = doGetProperty(realPropertyName);
    }
    return value;
  }

  @Override
  public String getPropertyAsString(String property) {
    if (convertIfIsStringValue) {
      Object unmaskValue = unmaskValue(getProperty(property));
      if (Objects.nonNull(unmaskValue)) {
        String realPropertyName = resolvePropertyExpression(property);
        Class<?> cls = getPropertyValueType(realPropertyName);
        unmaskValue = propertyConverterRegistry.getPropertyConverter(cls).asString(unmaskValue);
      }
      return (String) unmaskValue;
    }
    else {
      throw new ReflectionException("Don't support convert value as string");
    }
  }

  /**
   * 解析属性表达式，比如内嵌属性表达式，或者索引属性表达式
   */
  protected String resolvePropertyExpression(String propertyExpression) {
    if (StringUtils.isBlank(propertyExpression)) {
      throw new ReflectionException("Property expression or name can not be empty or null");
    }
    else {
      return propertyExpression;
    }
  }

  /**
   * 得到属性对应的值的类型，交给子类处理
   */
  protected abstract Class<?> getPropertyValueType(String property);

  /**
   * 真正实现获取属性值的方法，子类实现
   * @param realPropertyName 真正的属性名，已经解析过了的（该属性一定存在）
   * @return 对应的属性值
   */
  protected abstract Object doGetProperty(String realPropertyName);

  static Object maskValue(Object originValue) {
    return Optional.ofNullable(originValue).orElse(NULL_VALUE);
  }

  static Object unmaskValue(Object value) {
    return value == NULL_VALUE ? null : value;
  }
}