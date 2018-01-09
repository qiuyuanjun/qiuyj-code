package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/8
 */
public class MapWrapperImpl extends IndexedPropertyAccessor implements ObjectWrapper<Map<String, Object>> {

  private final Map<String, Object> map;

  private final Class<Map<String, Object>> mapType;

  @SuppressWarnings("unchecked")
  public MapWrapperImpl(Map<String, Object> map) {
    this.map = Objects.requireNonNull(map);
    this.mapType = (Class<Map<String, Object>>) map.getClass();
  }

  @Override
  protected void doSetNestedProperty(String nestedProperty, Object value) {
    map.put(nestedProperty, value);
  }

  @Override
  protected Object doGetNestedProperty(String property) {
    return map.get(property);
  }

  @Override
  public Map<String, Object> getWrappedInstance() {
    return map;
  }

  @Override
  public Class<Map<String, Object>> getWrappedClass() {
    return mapType;
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
}