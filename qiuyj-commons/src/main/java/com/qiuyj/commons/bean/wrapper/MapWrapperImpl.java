package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.IndexedPropertyAccessor;
import com.qiuyj.commons.bean.PropertyAccessorSupport;
import com.qiuyj.commons.bean.ReflectionException;
import com.qiuyj.commons.bean.ResolvableType;

import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
@SuppressWarnings("unchecked")
public class MapWrapperImpl<T> extends IndexedPropertyAccessor implements IndexedObjectWrapper<T>, WrappedObjectInfo<Map<String, T>> {

  private final Map<String, T> map;

  private final Class<Map<String, T>> mapClass;

  private Class<T> valueType;

  public MapWrapperImpl(Map<String, T> map) {
    this.map = Objects.requireNonNull(map);
    mapClass = (Class<Map<String, T>>) map.getClass();
  }

  public MapWrapperImpl(Map<String, T> map, ResolvableType mapResolvableType) {
    this(map);
    valueType = (Class<T>) mapResolvableType.resolveGenericAt(1);
  }

  @Override
  protected void setDirectPropertyValue(String realPropertyName, Object value) {
    if (Objects.nonNull(valueType) && value != PropertyAccessorSupport.NULL_VALUE) {
      Class<?> currValueType = value.getClass();
      if (currValueType != valueType && !valueType.isAssignableFrom(currValueType)) {
        throw new ReflectionException("Type not match. Expected type is: " + valueType + ", but actual is: " + currValueType);
      }
    }
  }

  @Override
  protected Object getDirectProperty(String directPropertName) {
    return map.get(directPropertName);
  }

  /**
   * 该方法应该不会调用
   */
  @Override
  protected Class<?> getPropertyValueType(String property) {
    return Objects.isNull(valueType) ?
        Objects.isNull(getProperty(property)) ?
            Object.class : getProperty(property).getClass()
        : valueType;
  }

  @Override
  public Class<T> getIndexedPropertyValueType() {
    return valueType;
  }

  public Class<Map<String, T>> getWrappedClass() {
    return mapClass;
  }

  public Map<String, T> getWrappedInstance() {
    return map;
  }
}