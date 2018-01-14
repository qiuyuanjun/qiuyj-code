package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.bean.IndexedPropertyAccessor;
import com.qiuyj.commons.bean.PropertyAccessorSupport;
import com.qiuyj.commons.bean.ReflectionException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
@SuppressWarnings("unchecked")
public class BeanWrapperImpl<T> extends IndexedPropertyAccessor implements BeanWrapper<T> {

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
  public PropertyDescriptor[] getPropertyDescriptors() {
    return cachedIntrospectResult.getBeanInfo().getPropertyDescriptors();
  }

  @Override
  public PropertyDescriptor getPropertyDescriptor(String property) {
    return cachedIntrospectResult.getPropertyDescriptor(property);
  }

  @Override
  public Class<T> getWrappedClass() {
    return beanCls;
  }

  @Override
  public T getWrappedInstance() {
    return bean;
  }

  @Override
  protected Class<?> getPropertyValueType(String property) {
    PropertyDescriptor pd = getPropertyDescriptor(property);
    if (Objects.isNull(pd)) {
      throw new ReflectionException("Can not find property '" + property + "' in class: " + beanCls);
    }
    else {
      return pd.getPropertyType();
    }
  }

  @Override
  protected void setDirectPropertyValue(String property, Object value) {
    PropertyDescriptor pd = getPropertyDescriptor(property);
    if (Objects.isNull(pd)) {
      throw new ReflectionException("Can not find property '" + property + "' in class: " + beanCls);
    }
    else {
      // 判断类型是否一致
      if (value != PropertyAccessorSupport.NULL_VALUE) {
        Class<?> propertyType = pd.getPropertyType();
        validateType(propertyType, value.getClass());
      }
      // 设置值
      ReflectionUtils.invokeMethod(bean, pd.getWriteMethod(), value);
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
  protected Object getDirectProperty(String directPropertName) {
    PropertyDescriptor pd = getPropertyDescriptor(directPropertName);
    if (Objects.nonNull(pd)) {
      return ReflectionUtils.invokeMethod(bean, pd.getReadMethod());
    }
    else {
      throw new ReflectionException("Can not found property: " + directPropertName + " in object: " + bean);
    }
  }
}