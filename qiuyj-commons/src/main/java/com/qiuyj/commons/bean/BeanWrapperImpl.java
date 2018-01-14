package com.qiuyj.commons.bean;

import com.qiuyj.commons.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 请一定注意，该功能是线程不安全的，请一定要杜绝该类作为共享变量，应该使其作为方法的局部变量
 * @author qiuyj
 * @since 2018/1/4
 */
@SuppressWarnings("unchecked")
public class BeanWrapperImpl<T> extends IndexedPropertyAccessor implements ObjectWrapper<T> {

  private final T bean;

  private final Class<T> beanCls;

  private final CachedIntrospectResult cachedIntrospectResult;

  private Map<String, Type> indexedGenericPropertyMap;

  public BeanWrapperImpl(T bean) {
    this.bean = Objects.requireNonNull(bean);
    beanCls = (Class<T>) bean.getClass();
    cachedIntrospectResult = new CachedIntrospectResult(beanCls);
  }

  @Override
  public T getWrappedInstance() {
    return bean;
  }

  @Override
  public Class<T> getWrappedClass() {
    return beanCls;
  }

  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    return cachedIntrospectResult.getBeanInfo().getPropertyDescriptors();
  }

  @Override
  public PropertyDescriptor getPropertyDescriptor(String property) {
    return cachedIntrospectResult.getPropertyDescriptor(property);
  }

  @Override
  protected void doSetNestedProperty(String nestedProperty, Object value) {
    PropertyDescriptor pd = getPropertyDescriptor(nestedProperty);
    if (Objects.nonNull(pd)) {
      // 判断类型是否一致
      if (Objects.nonNull(value)) {
        Class<?> propertyType = pd.getPropertyType();
        validateType(propertyType, value.getClass());
      }
      // 设置值
      ReflectionUtils.invokeMethod(bean, pd.getWriteMethod(), value);
    }
    else {
      throw new ReflectionException("Can not found property: " + nestedProperty + " in object: " + bean);
    }
  }

  @Override
  protected Object doGetNestedProperty(String nestedProperty) {
    PropertyDescriptor pd = getPropertyDescriptor(nestedProperty);
    if (Objects.nonNull(pd)) {
      return ReflectionUtils.invokeMethod(bean, pd.getReadMethod());
    }
    else {
      throw new ReflectionException("Can not found property: " + nestedProperty + " in object: " + bean);
    }
  }

  public static void validateType(Class<?> originType, Class<?> setValueType) {
    if (!typeCheck(originType, setValueType)) {
      throw new ReflectionException("Type not match. Expected: " + originType + ", acutal: " + setValueType);
    }
  }

  private static boolean typeCheck(Class<?> expectedType, Class<?> actualType) {
    boolean same = true;
    if (expectedType != actualType) {
      if (actualType.isPrimitive()) {
        same = primitiveBoxingTypeEq(expectedType, actualType);
      }
      else if (expectedType.isPrimitive()) {
        same = primitiveBoxingTypeEq(actualType, expectedType);
      }
      else if (expectedType.isArray() && actualType.isArray()) {
        same = typeCheck(expectedType.getComponentType(), actualType.getComponentType());
      }
      else {
        same = expectedType.isAssignableFrom(actualType);
      }
    }
    return same;
  }

  private static boolean primitiveBoxingTypeEq(Class<?> boxingType, Class<?> primitiveType) {
    return (boxingType == Integer.class && primitiveType == Integer.TYPE)
        || (boxingType == Long.class && primitiveType == Long.TYPE)
        || (boxingType == Byte.class && primitiveType == Byte.TYPE)
        || (boxingType == Boolean.class && primitiveType == Boolean.TYPE)
        || (boxingType == Character.class && primitiveType == Character.TYPE)
        || (boxingType == Short.class && primitiveType == Short.TYPE)
        || (boxingType == Float.class && primitiveType == Float.TYPE)
        || (boxingType == Double.class && primitiveType == Double.TYPE);
  }

  @Override
  protected Class<?> getPropertyType(String property) {
    PropertyDescriptor pd = getPropertyDescriptor(property);
    if (Objects.isNull(pd)) {
      throw new IllegalStateException("There are no property found");
    }
    else {
      return pd.getPropertyType();
    }
  }

  @Override
  protected Type getIndexedPropertyGenericType(String propertyName) {
    if (Objects.isNull(indexedGenericPropertyMap)) {
      indexedGenericPropertyMap = new HashMap<>();
    }
    Type indexedGenericType = indexedGenericPropertyMap.get(propertyName);
    if (Objects.isNull(indexedGenericType)) {
      Field field = ReflectionUtils.getDeclaredField(beanCls, propertyName);
      indexedGenericType = field.getGenericType();
      indexedGenericPropertyMap.put(propertyName, indexedGenericType);
    }
    return indexedGenericType;
  }
}