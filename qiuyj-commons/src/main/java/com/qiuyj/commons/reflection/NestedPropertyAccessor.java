package com.qiuyj.commons.reflection;

import com.qiuyj.commons.ReflectionUtils;

import java.lang.reflect.Array;
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

  private final Map<String, NestedProperty> nestedRootProperty;

  protected NestedPropertyAccessor() {
    autoInstantiateNestedPropertyNullValue = true;
    nestedRootProperty = new HashMap<>();
  }

  @Override
  public void setAutoInstantiateNestedPropertyNullValue(boolean autoInstantiateNestedPropertyNullValue) {
    this.autoInstantiateNestedPropertyNullValue = autoInstantiateNestedPropertyNullValue;
  }

  @Override
  protected boolean doSetProperty(String property, Object value) {
    boolean realSetValue = false;
    if (Objects.isNull(value)) {
      // 如果设置的值是null，那么移除对应的nestedPropertyValue
      nestedRootProperty.remove(property);
      realSetValue = true;
    }
    else {
      NestedProperty nestedProperty = nestedRootProperty.get(property);
      if (Objects.nonNull(nestedProperty)) {
        nestedProperty.getRoot().setProperty(getNestedOrIndexedPropertyName(nestedProperty), value);
      }
      else {
        realSetValue = true;
      }
    }
    if (realSetValue) {
      doSetNestedProperty(property, value);
    }
    return realSetValue;
  }

  protected abstract void doSetNestedProperty(String nestedProperty, Object value);

  private static String getNestedOrIndexedPropertyName(NestedProperty nestedProperty) {
    String indexedOrNestedPropertyName;
    if (nestedProperty instanceof IndexedProperty) {
      indexedOrNestedPropertyName = ((IndexedProperty) nestedProperty).getIndexedPropertyName();
    }
    else {
      indexedOrNestedPropertyName = nestedProperty.getNestedPropertyName();
    }
    return indexedOrNestedPropertyName;
  }

  @Override
  protected Object doGetProperty(String property) {
    NestedProperty nestedProperty = nestedRootProperty.get(property);
    Object propertyValue;
    if (Objects.nonNull(nestedProperty)) {
      propertyValue = nestedProperty.getRoot().getProperty(getNestedOrIndexedPropertyName(nestedProperty));
    }
    else {
      propertyValue = doGetNestedProperty(property);
    }
    return propertyValue;
  }

  protected abstract Object doGetNestedProperty(String property);

  @Override
  protected String resolvePropertyName(String propertyName) {
    propertyName = super.resolvePropertyName(propertyName);
    int dotIdx = propertyName.indexOf(PropertyAccessor.NESTED_PROPERTY_SEPERATOR_STRING);
    if (dotIdx > 0) {
      String realPropertyName = propertyName.substring(0, dotIdx);
      realPropertyName = resolveIndexedPropertyName(realPropertyName);
      Object realPropertyValue = getOrInitPropertyValue(realPropertyName);
      propertyName = propertyName.substring(dotIdx + 1);
      NestedProperty nestedProperty = nestedRootProperty.get(realPropertyName);
      if (Objects.isNull(nestedProperty)) {
        /*
         * 如果此时NestedProperty为null，那么表明当前的属性一定不是Map，Collection或者数组
         * 那么直接对应的是BeanWrapperImpl，无需判断realPropertyValue的类型，然后对应实例wrapper对象
         */
        nestedProperty = new NestedProperty(new BeanWrapperImpl<>(realPropertyValue), propertyName);
        nestedRootProperty.put(realPropertyName, nestedProperty);
      }
      else {
        nestedProperty.setNestedPropertyName(propertyName);
      }
      propertyName = realPropertyName;
    }
    else if (dotIdx == 0) {
      throw new ReflectionException("Nested property can not be empty. May caused by the nested property flag '.' at the first location of the property string");
    }
    else {
      propertyName = resolveIndexedPropertyName(propertyName);
    }
    return propertyName;
  }

  protected Object getOrInitPropertyValue(String propertyName) {
    Object realPropertyValue = getProperty(propertyName);
    if (Objects.isNull(realPropertyValue)) {
      if (!autoInstantiateNestedPropertyNullValue) {
        throw new IllegalStateException("Not support null nested property value");
      }
      else {
        Class<?> type = getPropertyType(propertyName);
        if (type.isArray()) {
          // 如果是数组，那么需要初始化数组，并且给定大小，默认给32个长度
          realPropertyValue = Array.newInstance(type.getComponentType(), 32);
        }
        else {
          realPropertyValue = ReflectionUtils.instantiateClass(type);
        }
        setProperty(propertyName, realPropertyValue);
      }
    }
    return realPropertyValue;
  }

  protected void setNestedProperty(String property, NestedProperty nestedProperty) {
    nestedRootProperty.put(property, nestedProperty);
  }

  protected abstract String resolveIndexedPropertyName(String indexedPropertyName);
}