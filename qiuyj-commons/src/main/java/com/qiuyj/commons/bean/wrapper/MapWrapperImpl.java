package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.AbstractNestedPropertyAccessor;
import com.qiuyj.commons.bean.ResolvableType;

import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/19
 */
@SuppressWarnings("unchecked")
public class MapWrapperImpl extends AbstractNestedPropertyAccessor implements IndexedObjectWrapper {

  private Class<?> valueType;

  public MapWrapperImpl(Class<? extends Map<String, ?>> wrappedClass) {
    super(wrappedClass);
  }

  public MapWrapperImpl(Map<String, ?> map) {
    super(map);
  }

  /**
   * 该构造函数仅仅用于内部使用
   */
  public MapWrapperImpl(Map map, ResolvableType mapResolvableType) {
    this(map);
    valueType = mapResolvableType.resolveGenericAt(1);
  }

  @Override
  protected void doSetPropertyValue(String property, Object value) {
    ((Map) wrappedInstance).put(property, value);
  }

  @Override
  protected Object doGetPropertyValue(String property) {
    return ((Map) wrappedInstance).get(property);
  }

  @Override
  protected Class<?> getPropertyType(String property) {
    return getIndexedPropertyValueType();
  }

  @Override
  public Class<?> getIndexedPropertyValueType() {
    return Objects.isNull(valueType) ? Object.class : valueType;
  }
}