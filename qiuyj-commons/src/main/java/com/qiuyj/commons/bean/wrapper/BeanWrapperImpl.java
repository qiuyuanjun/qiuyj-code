package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.bean.AbstractNestedPropertyAccessor;
import com.qiuyj.commons.bean.CachedIntrospectorResults;
import com.qiuyj.commons.bean.exception.ReflectionException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/19
 */
public class BeanWrapperImpl extends AbstractNestedPropertyAccessor implements BeanWrapper {

  private final CachedIntrospectorResults introspectorResults;

  /**
   * 默认不支持字段操作，必须得通过对应的getter和setter
   */
  private boolean fieldOperationSupport = false;

  public BeanWrapperImpl(Class<?> wrappedClass) {
    super(wrappedClass);
    introspectorResults = CachedIntrospectorResults.forClass(wrappedClass);
  }

  public BeanWrapperImpl(Object wrappedInstance) {
    super(wrappedInstance);
    introspectorResults = CachedIntrospectorResults.forClass(wrappedClass);
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
      if (pd instanceof NoGetterSetterPropertyDescriptor) {
        Field propertyField = ((NoGetterSetterPropertyDescriptor) pd).getPropertyField();
        ReflectionUtils.makeAccessible(propertyField);
        try {
          propertyField.set(wrappedInstance, value);
        }
        catch (IllegalAccessException e) {
          // ignore
        }
      }
      else {
        Method writeMethod = pd.getWriteMethod();
        if (Objects.isNull(writeMethod)) {
          if (fieldOperationSupport) {
            Field propertyField = ReflectionUtils.getDeclaredField(wrappedClass, property);
            ReflectionUtils.makeAccessible(propertyField);
            try {
              propertyField.set(wrappedInstance, value);
            }
            catch (IllegalAccessException e) {
              // ingore
            }
          }
          else {
            throw new IllegalStateException("Property '" + property + "' is an readonly property.");
          }
        }
        else {
          ReflectionUtils.makeAccessible(writeMethod);
          ReflectionUtils.invokeMethod(wrappedInstance, writeMethod, value);
        }
      }
    }
  }

  @Override
  protected Object doGetPropertyValue(String property) {
    PropertyDescriptor pd = getPropertyDescriptor(property);
    Object getterValue = null;
    if (Objects.isNull(pd)) {
      throw new ReflectionException("Can not found property: " + property + " in class: " + wrappedClass);
    }
    else if (pd instanceof NoGetterSetterPropertyDescriptor) {
      Field propertyField = ((NoGetterSetterPropertyDescriptor) pd).getPropertyField();
      ReflectionUtils.makeAccessible(propertyField);
      try {
        getterValue = propertyField.get(wrappedInstance);
      }
      catch (IllegalAccessException e) {
        // ignore
      }
    }
    else {
      Method readMethod = pd.getReadMethod();
      if (Objects.isNull(readMethod)) {
        if (fieldOperationSupport) {
          Field propertyField = ReflectionUtils.getDeclaredField(wrappedClass, property);
          ReflectionUtils.makeAccessible(propertyField);
          try {
            getterValue = propertyField.get(wrappedInstance);
          }
          catch (IllegalAccessException e) {
            // ignore
          }
        }
        else {
          throw new ReflectionException("Property '" + property + "' is an reading invisible property.");
        }
      }
      else {
        ReflectionUtils.makeAccessible(readMethod);
        getterValue = ReflectionUtils.invokeMethod(wrappedInstance, readMethod);
      }
    }
    return getterValue;
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
    PropertyDescriptor pd = introspectorResults.getPropertyDescriptor(property);
    if (Objects.isNull(pd) && fieldOperationSupport) {
      Field propertyField = ReflectionUtils.getDeclaredFieldIfAvaliable(wrappedClass, property);
      if (Objects.nonNull(propertyField)) {
        try {
          pd = new NoGetterSetterPropertyDescriptor(property, propertyField);
        }
        catch (IntrospectionException e) {
          throw new ReflectionException("Error getting '" + property + "'s property descriptor.\n Caused by: " + e, e);
        }
      }
    }
    return pd;
  }

  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    return introspectorResults.getBeanInfo().getPropertyDescriptors();
  }

  @Override
  public void setFieldOperationSupport(boolean fieldOperationSupport) {
    this.fieldOperationSupport = fieldOperationSupport;
  }
}