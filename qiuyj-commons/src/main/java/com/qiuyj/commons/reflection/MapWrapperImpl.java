package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/8
 */
public class MapWrapperImpl extends NestedPropertyAccessor implements ObjectWrapper<Map<?, ?>> {

  private final Map<?, ?> map;

  private final Class<Map<?, ?>> mapType;

  @SuppressWarnings("unchecked")
  public MapWrapperImpl(Map<?, ?> map) {
    this.map = Objects.requireNonNull(map);
    this.mapType = (Class<Map<?, ?>>) map.getClass();
  }

  @Override
  protected void doSetNestedProperty(String nestedProperty, Object value) {

  }

  @Override
  protected Object doGetNestedProperty(String property) {
    return null;
  }

  @Override
  public Map<?, ?> getWrappedInstance() {
    return map;
  }

  @Override
  public Class<Map<?, ?>> getWrappedClass() {
    return mapType;
  }

  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    return new PropertyDescriptor[0];
  }

  @Override
  public PropertyDescriptor getPropertyDescriptor(String property) {
    return null;
  }

  @Override
  protected Class<?> getPropertyType(String property) {
    return null;
  }
}