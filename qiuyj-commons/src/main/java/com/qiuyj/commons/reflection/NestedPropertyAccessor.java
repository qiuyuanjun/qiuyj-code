package com.qiuyj.commons.reflection;

import com.qiuyj.commons.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 内嵌属性访问器
 * @author qiuyj
 * @since 2018/1/5
 */
public abstract class NestedPropertyAccessor extends PropertyAccessorSupport {

  private boolean autoInstantiateNestedPropertyNullValue;

  private final Map<String, PropertyAccessor> nestedRootPropertyValues;

  protected NestedPropertyAccessor() {
    autoInstantiateNestedPropertyNullValue = true;
    nestedRootPropertyValues = new HashMap<>();
  }

  @Override
  public void setAutoInstantiateNestedPropertyNullValue(boolean autoInstantiateNestedPropertyNullValue) {
    this.autoInstantiateNestedPropertyNullValue = autoInstantiateNestedPropertyNullValue;
  }

  @Override
  protected void doSetProperty(String property, Object value) {
    if (Objects.isNull(value)) {
      // 如果设置的值是null，那么移除对应的nestedPropertyValue
      nestedRootPropertyValues.remove(property);
    }
  }

  @Override
  protected String resolvePropertyName(String propertyName) {
    propertyName = super.resolvePropertyName(propertyName);
    int dotIdx = propertyName.indexOf(PropertyAccessor.NESTED_PROPERTY_SEPERATOR_STRING);
    if (dotIdx > 0) {
      String realPropertyName = propertyName.substring(dotIdx);
      Object realPropertyValue = getProperty(realPropertyName);
      if (!nestedRootPropertyValues.containsKey(realPropertyName)) {
        if (Objects.isNull(realPropertyValue)) {
          if (!autoInstantiateNestedPropertyNullValue) {
            throw new IllegalStateException("Not support null nested property value");
          }
          else {
            realPropertyValue = ReflectionUtils.instantiateClass(getPropertyType(propertyName));
          }
        }
        nestedRootPropertyValues.put(realPropertyName, new BeanWrapperImpl<>(realPropertyValue));
      }
      propertyName = realPropertyName;
    }
    else if (dotIdx == 0) {
      propertyName = propertyName.substring(1);
    }
    return propertyName;
  }
}