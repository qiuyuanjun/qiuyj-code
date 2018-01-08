package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/8
 */
public class CollectionWrapperImpl extends NestedPropertyAccessor implements ObjectWrapper<Collection<?>> {

  private final Collection<?> collection;

  private final Class<Collection<?>> collectionType;

  @SuppressWarnings("unchecked")
  public CollectionWrapperImpl(Collection<?> collection) {
    this.collection = Objects.requireNonNull(collection);
    collectionType = (Class<Collection<?>>) collection.getClass();
  }

  @Override
  protected void doSetNestedProperty(String nestedProperty, Object value) {

  }

  @Override
  protected Object doGetNestedProperty(String property) {
    return null;
  }

  @Override
  public Collection<?> getWrappedInstance() {
    return collection;
  }

  @Override
  public Class<Collection<?>> getWrappedClass() {
    return collectionType;
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