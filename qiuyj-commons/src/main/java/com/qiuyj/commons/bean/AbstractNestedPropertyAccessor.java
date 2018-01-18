package com.qiuyj.commons.bean;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.bean.exception.NestedValueIsNullException;
import com.qiuyj.commons.bean.exception.ReflectionException;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/18
 */
public abstract class AbstractNestedPropertyAccessor extends PropertyAccessorSupport {

  private boolean autoInstantiationNestedNullValue = true;

  protected final Object wrappedInstance;

  protected final Class<?> wrappedClass;

  private Map<String, AbstractNestedPropertyAccessor> nestedPropertyAccessors;

  protected AbstractNestedPropertyAccessor(Class<?> wrappedClass) {
    this.wrappedClass = Objects.requireNonNull(wrappedClass);
    wrappedInstance = ReflectionUtils.instantiateClass(wrappedClass);
  }

  protected AbstractNestedPropertyAccessor(Object wrappedInstance) {
    this.wrappedInstance = Objects.requireNonNull(wrappedInstance);
    wrappedClass = wrappedInstance.getClass();
  }

  @Override
  public void setAutoInstantiationNestedNullValue(boolean autoInstantiationNestedNullValue) {
    this.autoInstantiationNestedNullValue = autoInstantiationNestedNullValue;
  }

  @Override
  public void setPropertyValue(String property, Object value) {
    SinglePropertyHolder sph = parseProperty(property);
    AbstractNestedPropertyAccessor nestedPropertyAccessor = null;
    String nestedPath = null;
    if (Objects.isNull(sph.getNestedPropertyPath()) && Objects.isNull(sph.getIndexedProperty())) {
      doSetPropertyValue(sph.getCurrentProperty(), value);
      if (Objects.isNull(value)) {
        nestedPropertyAccessors.remove(sph.getCurrentProperty());
      }
    }
    else if (Objects.isNull(sph.getIndexedProperty())) {
      nestedPropertyAccessor = getNestedPropertyAccessor(sph.getCurrentProperty());
      nestedPath = sph.getNestedPropertyPath();
    }
    else if (Objects.isNull(sph.getNestedPropertyPath())) {
      nestedPropertyAccessor = getNestedPropertyAccessor(sph.getCurrentProperty());
      // TODO check whether is map, list or array?
      nestedPath = sph.getIndexedProperty();
    }
    else {
      nestedPropertyAccessor = getNestedPropertyAccessor(sph.getCurrentProperty());
      // TODO check whether is map, list or array?
      nestedPath = sph.getIndexedProperty() + sph.getNestedPropertyPath();
    }
    if (Objects.nonNull(nestedPropertyAccessor)) {
      nestedPropertyAccessor.setPropertyValue(nestedPath, value);
    }
  }

  protected abstract AbstractNestedPropertyAccessor newNestedPropertyAccessor(Object value);

  private AbstractNestedPropertyAccessor getNestedPropertyAccessor(String nestedProperty) {
    AbstractNestedPropertyAccessor nestedPropertyAccessor = null;
    if (Objects.isNull(nestedPropertyAccessors)) {
      nestedPropertyAccessors = new HashMap<>();
    }
    else {
      nestedPropertyAccessor = nestedPropertyAccessors.get(nestedProperty);
    }
    if (Objects.isNull(nestedPropertyAccessor)) {
      Object currValue = getPropertyValue(nestedProperty);
      if (Objects.isNull(currValue)) {
        if (!autoInstantiationNestedNullValue) {
          throw new NestedValueIsNullException(wrappedInstance, nestedProperty);
        }
        else {
          Class<?> nestedPropertyType = getPropertyType(nestedProperty);
          if (nestedPropertyType.isArray()) {
            currValue = Array.newInstance(nestedPropertyType.getComponentType(), 1);
          }
          else {
            currValue = ReflectionUtils.instantiateClass(nestedPropertyType);
          }
        }
      }
      nestedPropertyAccessor = newNestedPropertyAccessor(currValue);
      nestedPropertyAccessors.put(nestedProperty, nestedPropertyAccessor);
    }
    return nestedPropertyAccessor;
  }

  protected abstract void doSetPropertyValue(String property, Object value);

  @Override
  public Object getPropertyValue(String property) {
    SinglePropertyHolder sph = parseProperty(property);
    return null;
  }

  @Override
  public Object getWrappedInstance() {
    return wrappedInstance;
  }

  @Override
  public Class<?> getWrappedClass() {
    return wrappedClass;
  }

  private static SinglePropertyHolder parseProperty(String property) {
    Objects.requireNonNull(property);
    int flagIdx = property.indexOf(PropertyAccessor.NESTED_PROPERTY_SEPARATOR);
    String currentProperty = property,
           nestedPropertyPath = null,
           indexedProperty = null;
    if (flagIdx > 0) {
      currentProperty = property.substring(0, flagIdx);
      nestedPropertyPath = property.substring(flagIdx + 1);
    }
    else if (flagIdx == 0) {
      throw new ReflectionException("Nested property separator '.' can not be located at the first location of this property expression: " + property);
    }
    flagIdx = currentProperty.indexOf(PropertyAccessor.INDEXED_PROPERTY_PREFIX);
    if (flagIdx > 0) {
      if (currentProperty.charAt(currentProperty.length() - 1) != PropertyAccessor.INDEXED_PROPERTY_SUFFIX) {
        throw new ReflectionException("Indexed property's last character must be ']'");
      }
      else {
        indexedProperty = currentProperty.substring(flagIdx + 1, currentProperty.length() - 1);
        currentProperty = currentProperty.substring(0, flagIdx);
      }
    }
    else if (flagIdx == 0) {
      throw new ReflectionException("Indexed property prefix '[' can not be located at the first location of this property: " + currentProperty);
    }
    return new SinglePropertyHolder(currentProperty, nestedPropertyPath, indexedProperty);
  }
}