package com.qiuyj.commons.reflection.wrap;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.reflection.ObjectWrapper;
import com.qiuyj.commons.reflection.PropertyAccessor;
import com.qiuyj.commons.reflection.defaultimpl.PropertyAccessorSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/3
 */
public class BeanWrapper<T> extends PropertyAccessorSupport implements ObjectWrapper<T>, PropertyAccessor {
  private final T bean;

  private final Class<T> beanCls;

//  private final BeanInfo beanInfo;

  @SuppressWarnings("unchecked")
  public BeanWrapper(T bean) {
    this.bean = bean;
    beanCls = (Class<T>) bean.getClass();

    /*
     * 这里不用BeanInfo，因为这个类只有在给了getter方法或setter方法的时候，才能获取到对应的属性
     */
//    try {
//      beanInfo = Introspector.getBeanInfo(beanCls);
//    } catch (IntrospectionException e) {
//      throw new ReflectionException("Error getting bean info.\n Caused by: " + e, e);
//    }
  }

  @Override
  public T getWrappedObject() {
    return bean;
  }

  @Override
  public void set(String property, Object value) {
    Objects.requireNonNull(property);
    String setterMethodName = fieldToMethodName(property, "set");
    Method propertyWriterMethod = null;
    try {
      propertyWriterMethod = ReflectionUtils.getDeclaredMethod(beanCls, setterMethodName, value.getClass());
    }
    catch (IllegalStateException e) {
      // ignore
    }
    if (Objects.nonNull(propertyWriterMethod)) {
      ReflectionUtils.makeAccessible(propertyWriterMethod);
      ReflectionUtils.invokeMethod(bean, propertyWriterMethod, value);
    }
    else {
      Field currField = null;
      try {
        currField = ReflectionUtils.getDeclaredField(beanCls, property);
      }
      catch (IllegalStateException e) {
        // ignore
      }
      if (Objects.nonNull(currField)) {
        ReflectionUtils.makeAccessible(currField);
        try {
          currField.set(bean, value);
        } catch (IllegalAccessException e) {
          // ignore, never happen
        }
      }
    }
    addValue(property, value);
  }

  @Override
  public Object get(String property) {
    Object value = getValue(property);
    if (Objects.isNull(value)) {
      // 此时再通过反射获取
      // 首先获取property对应的getter方法
      // 如果没有对应的getter方法，那么就通过property的Field对象获取
      String getterMethodName = fieldToMethodName(property, "get");
      Method propertyReadMethod = null;
      try {
        propertyReadMethod = ReflectionUtils.getDeclaredMethod(beanCls, getterMethodName);
      }
      catch (IllegalStateException e) {
        // ignore
      }
      if (Objects.nonNull(propertyReadMethod)) {
        ReflectionUtils.makeAccessible(propertyReadMethod);
        value = ReflectionUtils.invokeMethod(bean, propertyReadMethod);
      }
      else {
        // 通过Field字段获取
        Field currField = null;
        try {
          currField = ReflectionUtils.getDeclaredField(beanCls, property);
        }
        catch (IllegalStateException e) {
          // ignore
        }
        if (Objects.nonNull(currField)) {
          ReflectionUtils.makeAccessible(currField);
          try {
            value = currField.get(bean);
          } catch (IllegalAccessException e) {
            // ignore, never happen
          }
        }
      }
      if (Objects.nonNull(value)) {
        addValue(property, value);
      }
    }
    return value;
  }

  private String fieldToMethodName(String property, String type) {
    char[] chs = property.toCharArray();
    chs[0] = Character.toUpperCase(chs[0]);
    return type + new String(chs);
  }
}