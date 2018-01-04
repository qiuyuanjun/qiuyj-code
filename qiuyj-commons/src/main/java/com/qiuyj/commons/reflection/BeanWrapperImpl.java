package com.qiuyj.commons.reflection;

import java.beans.PropertyDescriptor;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
@SuppressWarnings("unchecked")
public class BeanWrapperImpl<T> extends PropertyAccessorSupport implements ObjectWrapper<T> {

  private final T bean;

  private final Class<T> beanCls;

  public BeanWrapperImpl(T bean) {
    this.bean = bean;
    beanCls = (Class<T>) bean.getClass();
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
    return new PropertyDescriptor[0];
  }

  @Override
  public PropertyDescriptor getPropertyDescriptor(String property) {
    return null;
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