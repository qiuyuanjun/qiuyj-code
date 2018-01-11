package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/8
 */
@SuppressWarnings("unchecked")
public class MapWrapperImpl<V> extends IndexedPropertyAccessor implements ObjectWrapper<Map<String, V>> {

  private final Map<String, V> map;

  private final Class<? extends Map<String, V>> mapType;

  private Class<?> valueType;

  public MapWrapperImpl(Map<String, V> map) {
    this.map = Objects.requireNonNull(map);
    mapType = (Class<? extends Map<String, V>>) map.getClass();
  }

  MapWrapperImpl(Map<String, V> map, ResolvableType mapResolvableType) {
    this.map = map;
    mapType = (Class<? extends Map<String, V>>) map.getClass();
    valueType = mapResolvableType.resolveGenericAt(1);
  }

  @Override
  protected void doSetNestedProperty(String nestedProperty, Object value) {
    if (Objects.nonNull(valueType) && Objects.nonNull(value)) {
      Class<?> currValueType = value.getClass();
      if (currValueType != valueType && !valueType.isAssignableFrom(currValueType)) {
        throw new ReflectionException("Type not match. Expected type is: " + valueType + ", but actual is: " + currValueType);
      }
    }
    map.put(nestedProperty, (V) value);
  }

  @Override
  protected Object doGetNestedProperty(String property) {
    return map.get(property);
  }

  @Override
  public Map getWrappedInstance() {
    return map;
  }

  @Override
  public Class<Map<String, V>> getWrappedClass() {
    return (Class<Map<String, V>>) mapType;
  }

  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    throw new UnsupportedOperationException();
  }

  @Override
  public PropertyDescriptor getPropertyDescriptor(String property) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Class<?> getPropertyType(String property) {
    return doGetNestedProperty(property).getClass();
  }

  @Override
  protected Type getIndexedPropertyGenericType(String propertyName) {
    throw new UnsupportedOperationException();
  }
}