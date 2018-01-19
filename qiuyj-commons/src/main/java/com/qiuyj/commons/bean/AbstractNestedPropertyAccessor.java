package com.qiuyj.commons.bean;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.bean.exception.NestedValueIsNullException;
import com.qiuyj.commons.bean.exception.ReflectionException;
import com.qiuyj.commons.bean.wrapper.BeanWrapperImpl;
import com.qiuyj.commons.bean.wrapper.IndexedObjectWrapper;
import com.qiuyj.commons.bean.wrapper.ListArrayWrapperImpl;
import com.qiuyj.commons.bean.wrapper.MapWrapperImpl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/18
 */
public abstract class AbstractNestedPropertyAccessor extends PropertyAccessorSupport {

  private boolean autoInstantiationNestedNullValue = true;

  protected Object wrappedInstance;

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
      if (Objects.isNull(value) && Objects.nonNull(nestedPropertyAccessors)) {
        nestedPropertyAccessors.remove(sph.getCurrentProperty());
      }
    }
    else if (Objects.isNull(sph.getIndexedProperty())) {
      nestedPropertyAccessor = getNestedPropertyAccessor(sph.getCurrentProperty());
      nestedPath = sph.getNestedPropertyPath();
    }
    else if (Objects.isNull(sph.getNestedPropertyPath())) {
      nestedPropertyAccessor = getNestedPropertyAccessor(sph.getCurrentProperty());
      requireMustBeIndexedObjectWrapper(nestedPropertyAccessor);
      nestedPath = sph.getIndexedProperty();
    }
    else if (Objects.nonNull(sph.getNestedPropertyPath()) && Objects.nonNull(sph.getIndexedProperty())) {
      nestedPropertyAccessor = getNestedPropertyAccessor(sph.getCurrentProperty());
      requireMustBeIndexedObjectWrapper(nestedPropertyAccessor);
      nestedPath = sph.getIndexedProperty() + PropertyAccessor.NESTED_PROPERTY_SEPARATOR + sph.getNestedPropertyPath();
    }
    if (Objects.nonNull(nestedPropertyAccessor)) {
      nestedPropertyAccessor.setPropertyValue(nestedPath, value);
    }
  }

  private static void requireMustBeIndexedObjectWrapper(AbstractNestedPropertyAccessor nestedPropertyAccessor) {
    if (!(nestedPropertyAccessor instanceof IndexedObjectWrapper)) {
      throw new ReflectionException("Indexed property access must be an instance of class 'IndexedObjectWrapper'");
    }
  }

  private AbstractNestedPropertyAccessor getNestedPropertyAccessor(String nestedProperty) {
    AbstractNestedPropertyAccessor nestedPropertyAccessor = null;
    if (Objects.isNull(nestedPropertyAccessors)) {
      nestedPropertyAccessors = new HashMap<>();
    }
    else {
      nestedPropertyAccessor = nestedPropertyAccessors.get(nestedProperty);
    }
    Class<?> nestedPropertyType = getPropertyType(nestedProperty);
    if (Objects.isNull(nestedPropertyAccessor)) {
      Object currValue = getPropertyValue(nestedProperty);
      if (Objects.isNull(currValue)) {
        if (!autoInstantiationNestedNullValue) {
          throw new NestedValueIsNullException(wrappedInstance, nestedProperty);
        }
        else if (nestedPropertyType.isArray()) {
          currValue = Array.newInstance(nestedPropertyType.getComponentType(), 1);
        }
        else {
          currValue = ReflectionUtils.instantiateClass(nestedPropertyType);
        }
        setPropertyValue(nestedProperty, currValue);
      }
      nestedPropertyAccessor = newNestedPropertyAccessor(nestedPropertyType, currValue, nestedProperty);
      nestedPropertyAccessors.put(nestedProperty, nestedPropertyAccessor);
    }
    return nestedPropertyAccessor;
  }

  protected abstract void doSetPropertyValue(String property, Object value);

  @SuppressWarnings("unchecked")
  protected AbstractNestedPropertyAccessor newNestedPropertyAccessor(Class<?> nestedPropertyType, Object nestedPropertyValue, String nestedPropertyName) {
    AbstractNestedPropertyAccessor nestedPropertyAccessor;
    if (Map.class.isAssignableFrom(nestedPropertyType)) {
      Field field = ReflectionUtils.getDeclaredField(wrappedClass, nestedPropertyName);
      nestedPropertyAccessor = new MapWrapperImpl((Map<String, ?>) nestedPropertyValue, ResolvableType.forField(field));
    }
    else if (List.class.isAssignableFrom(nestedPropertyType) || nestedPropertyType.isArray()) {
      Class<?> componentType;
      if (List.class.isAssignableFrom(nestedPropertyType)) {
        Field field = ReflectionUtils.getDeclaredField(wrappedClass, nestedPropertyName);
        componentType = ResolvableType.forField(field).resolveGenericAt(0);
      }
      else {
        componentType = nestedPropertyType.getComponentType();
      }
      nestedPropertyAccessor = new ListArrayWrapperImpl(nestedPropertyValue, componentType);
    }
    else {
      nestedPropertyAccessor = new BeanWrapperImpl(nestedPropertyValue);
    }
    return nestedPropertyAccessor;
  }

  @Override
  public Object getPropertyValue(String property) {
    SinglePropertyHolder sph = parseProperty(property);
    if (Objects.isNull(sph.getNestedPropertyPath()) && Objects.isNull(sph.getIndexedProperty())) {
      return doGetPropertyValue(sph.getCurrentProperty());
    }
    Object currValue = getPropertyValue(sph.getCurrentProperty());
    if (Objects.isNull(currValue)) {
      return null;
    }
    else {
      AbstractNestedPropertyAccessor nestedPropertyAccessor = null;
      if (Objects.isNull(nestedPropertyAccessors)) {
        nestedPropertyAccessors = new HashMap<>();
      }
      else {
        nestedPropertyAccessor = nestedPropertyAccessors.get(sph.getCurrentProperty());
      }
      if (Objects.isNull(nestedPropertyAccessor)) {
        nestedPropertyAccessor = newNestedPropertyAccessor(getPropertyType(sph.getCurrentProperty()), currValue, sph.getCurrentProperty());
        nestedPropertyAccessors.put(sph.getCurrentProperty(), nestedPropertyAccessor);
      }
      String nestedPath = null;
      if (Objects.isNull(sph.getIndexedProperty())) {
        nestedPath = sph.getNestedPropertyPath();
      }
      else if (Objects.isNull(sph.getNestedPropertyPath())) {
        nestedPath = sph.getIndexedProperty();
      }
      else if (Objects.nonNull(sph.getNestedPropertyPath()) && Objects.nonNull(sph.getIndexedProperty())) {
        nestedPath = sph.getIndexedProperty() + PropertyAccessor.NESTED_PROPERTY_SEPARATOR + sph.getNestedPropertyPath();
      }
      return nestedPropertyAccessor.getPropertyValue(nestedPath);
    }
  }

  protected abstract Object doGetPropertyValue(String property);

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