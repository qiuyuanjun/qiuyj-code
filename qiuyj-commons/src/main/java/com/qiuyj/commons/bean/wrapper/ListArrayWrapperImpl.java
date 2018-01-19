package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.AbstractNestedPropertyAccessor;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/19
 */
@SuppressWarnings("unchecked")
public class ListArrayWrapperImpl extends AbstractNestedPropertyAccessor implements IndexedObjectWrapper {

  private Class<?> componentType;

  public ListArrayWrapperImpl(Class<?> wrappedClass) {
    super(wrappedClass);
    if (wrappedClass.isArray()) {
      componentType = wrappedClass.getComponentType();
      wrappedInstance = Array.newInstance(componentType, 10);
    }
  }

  public ListArrayWrapperImpl(Object value) {
    super(value);
    if (wrappedClass.isArray()) {
      componentType = wrappedClass.getComponentType();
      wrappedInstance = Array.newInstance(componentType, 10);
    }
  }

  public ListArrayWrapperImpl(Object value, Class<?> componentType) {
    this(value);
    this.componentType = componentType;
  }

  @Override
  protected void doSetPropertyValue(String property, Object value) {
    int idx = Integer.parseInt(property);
    if (wrappedClass.isArray()) {
      int len = Array.getLength(wrappedInstance);
      if (len <= idx) {
        // 扩容
        len = len + (len >> 1);
        Object newArray = Array.newInstance(componentType, len);
        System.arraycopy(wrappedInstance, 0, newArray, 0, len);
        wrappedInstance = newArray;
      }
      Array.set(wrappedInstance, idx, value);
    }
    else {
      ((List) wrappedInstance).add(idx, value);
    }
  }

  @Override
  protected Object doGetPropertyValue(String property) {
    int idx = Integer.parseInt(property),
        len;
    if (wrappedClass.isArray()) {
      len = Array.getLength(wrappedInstance);
    }
    else {
      len = ((List) wrappedInstance).size();
    }
    if (idx >= len) {
      return null;
    }
    else if (wrappedClass.isArray()) {
      return Array.get(wrappedInstance, idx);
    }
    else {
      return ((List) wrappedInstance).get(idx);
    }
  }

  @Override
  protected Class<?> getPropertyType(String property) {
    return getIndexedPropertyValueType();
  }

  @Override
  public Class<?> getIndexedPropertyValueType() {
    return Objects.isNull(componentType) ? Object.class : componentType;
  }
}