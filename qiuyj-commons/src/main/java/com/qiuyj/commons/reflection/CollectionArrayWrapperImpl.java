package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/8
 */
public class CollectionArrayWrapperImpl extends IndexedPropertyAccessor implements ObjectWrapper<Object> {

  private final Object collectionOrArray;

  private final Class<Object> collectionOrArrayType;

  @SuppressWarnings("unchecked")
  public CollectionArrayWrapperImpl(Object collectionOrArray) {
    this.collectionOrArray = Objects.requireNonNull(collectionOrArray);
    collectionOrArrayType = (Class<Object>) collectionOrArray.getClass();
    if (!Collection.class.isAssignableFrom(collectionOrArrayType) && !collectionOrArrayType.isArray()) {
      throw new ReflectionException("Only support collection or array");
    }
  }

  @Override
  protected void doSetNestedProperty(String nestedProperty, Object value) {

  }

  @Override
  protected Object doGetNestedProperty(String property) {
    return null;
  }

  @Override
  public Object getWrappedInstance() {
    return collectionOrArray;
  }

  @Override
  public Class<Object> getWrappedClass() {
    return collectionOrArrayType;
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