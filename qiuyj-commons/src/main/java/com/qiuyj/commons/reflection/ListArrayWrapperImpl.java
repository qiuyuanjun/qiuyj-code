package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/8
 */
@SuppressWarnings("unchecked")
public class ListArrayWrapperImpl extends IndexedPropertyAccessor implements ObjectWrapper<Object> {

  private final Object collectionOrArray;

  private final Class<Object> collectionOrArrayType;

  private Integer ifArrayLen;

  private Class<?> listValueType;

  public ListArrayWrapperImpl(Object collectionOrArray) {
    this.collectionOrArray = Objects.requireNonNull(collectionOrArray);
    collectionOrArrayType = (Class<Object>) collectionOrArray.getClass();
    if (!List.class.isAssignableFrom(collectionOrArrayType) && !collectionOrArrayType.isArray()) {
      throw new ReflectionException("Only support list or array");
    }
    if (collectionOrArrayType.isArray()) {
      ifArrayLen = Array.getLength(collectionOrArray);
    }
  }

  ListArrayWrapperImpl(Object collectionOrArray, ResolvableType rt) {
    this(collectionOrArray);
    listValueType = rt.resolveGenericAt(0);
  }

  @Override
  protected void doSetNestedProperty(String nestedProperty, Object value) {
    // 得到下标
    int idx = Integer.parseInt(nestedProperty);
    if (Objects.nonNull(ifArrayLen)) {
      if (idx >= ifArrayLen) {
        throw new IndexOutOfBoundsException(idx + " >= " + ifArrayLen);
      }
      else {
        Array.set(collectionOrArray, idx, value);
      }
    }
    else {
      if (Objects.nonNull(listValueType) && Objects.nonNull(value)) {
        Class<?> currValueType = value.getClass();
        if (listValueType != currValueType && listValueType.isAssignableFrom(currValueType)) {
          throw new ReflectionException("Type not match. Expected type is: " + listValueType + ", but actual is: " + currValueType);
        }
      }
      ((List<Object>) collectionOrArray).add(idx, value);
    }
  }

  @Override
  protected Object doGetNestedProperty(String property) {
    int idx = Integer.parseInt(property);
    if (Objects.nonNull(ifArrayLen)) {
      if (idx >= ifArrayLen) {
        throw new IndexOutOfBoundsException(idx + " >= " + ifArrayLen);
      }
      else {
        return Array.get(collectionOrArray, idx);
      }
    }
    else {
      return ((List<Object>) collectionOrArray).get(idx);
    }
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