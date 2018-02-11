package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.TypeResolver;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/2/11
 */
class NoGetterSetterPropertyDescriptor extends PropertyDescriptor {

  private final Field propertyField;

  public NoGetterSetterPropertyDescriptor(String propertyName, Field propertyField) throws IntrospectionException {
    super(propertyName, null, null);
    this.propertyField = Objects.requireNonNull(propertyField);
  }

  public Field getPropertyField() {
    return propertyField;
  }

  @Override
  public synchronized Class<?> getPropertyType() {
    return TypeResolver.erase(propertyField.getGenericType());
  }
}