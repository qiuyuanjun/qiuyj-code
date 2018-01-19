package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.bean.AbstractNestedPropertyAccessor;
import com.qiuyj.commons.bean.CachedIntrospectorResults;
import com.qiuyj.commons.bean.exception.ReflectionException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/19
 */
public class BeanWrapperImpl extends AbstractNestedPropertyAccessor implements BeanWrapper {

  private final CachedIntrospectorResults introspectorResults;

  public BeanWrapperImpl(Class<?> wrappedClass) {
    super(wrappedClass);
    introspectorResults = new CachedIntrospectorResults(wrappedClass);
  }

  public BeanWrapperImpl(Object wrappedInstance) {
    super(wrappedInstance);
    introspectorResults = new CachedIntrospectorResults(wrappedClass);
  }

  @Override
  protected void doSetPropertyValue(String property, Object value) {
    PropertyDescriptor pd = getPropertyDescriptor(property);
    if (Objects.isNull(pd)) {
      throw new ReflectionException("Can not found property: " + property + " in class: " + wrappedClass);
    }
    else {
      if (Objects.nonNull(value)) {
        validateType(pd.getPropertyType(), value.getClass());
      }
      Method writeMethod = pd.getWriteMethod();
      ReflectionUtils.makeAccessible(writeMethod);
      ReflectionUtils.invokeMethod(wrappedInstance, writeMethod, value);
    }
  }

  @Override
  protected Object doGetPropertyValue(String property) {
    PropertyDescriptor pd = getPropertyDescriptor(property);
    if (Objects.isNull(pd)) {
      throw new ReflectionException("Can not found property: " + property + " in class: " + wrappedClass);
    }
    else {
      Method readMethod = pd.getReadMethod();
      ReflectionUtils.makeAccessible(readMethod);
      return ReflectionUtils.invokeMethod(wrappedInstance, readMethod);
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
      throw new ReflectionException("Can not find property '" + property + "' in class: " + wrappedClass);
    }
    else {
      return pd.getPropertyType();
    }
  }

  @Override
  public PropertyDescriptor getPropertyDescriptor(String property) {
    return introspectorResults.getPropertyDescriptor(property);
  }

  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    return introspectorResults.getBeanInfo().getPropertyDescriptors();
  }
}