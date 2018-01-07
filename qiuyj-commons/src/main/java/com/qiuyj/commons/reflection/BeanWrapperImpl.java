package com.qiuyj.commons.reflection;

import com.qiuyj.commons.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.Objects;

/**
 * 请一定注意，该功能是线程不安全的，请一定要杜绝该类作为共享变量，应该使其作为方法的局部变量
 * @author qiuyj
 * @since 2018/1/4
 */
@SuppressWarnings("unchecked")
public class BeanWrapperImpl<T> extends NestedPropertyAccessor implements ObjectWrapper<T> {

  private final T bean;

  private final Class<T> beanCls;

  private final CachedIntrospectResult cachedIntrospectResult;

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
      throw new ReflectionException("There are no property found");
    }
  }

  static void validateType(Class<?> originType, Class<?> setValueType) {
    boolean same = true;
    if (originType != setValueType) {
      if (setValueType.isPrimitive()) {
        same = primitiveBoxingTypeEq(originType, setValueType);
      }
      else if (originType.isPrimitive()) {
        same = primitiveBoxingTypeEq(setValueType, originType);
      }
      else {
        same = originType.isAssignableFrom(setValueType);
      }
    }
    if (!same) {
      throw new ReflectionException("Type not match");
    }
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
  protected Object doGetProperty(String property) {
    PropertyDescriptor pd = getPropertyDescriptor(property);
    if (Objects.nonNull(pd)) {
      return ReflectionUtils.invokeMethod(bean, pd.getReadMethod());
    }
    else {
      throw new ReflectionException("Can not found property: " + property + " in object: " + bean);
    }
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
}