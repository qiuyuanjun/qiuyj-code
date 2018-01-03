package com.qiuyj.commons.reflection.defaultimpl;

import com.qiuyj.commons.reflection.PropertyValue;
import com.qiuyj.commons.reflection.PropertyValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/3
 */
public class DefaultPropertyValues implements PropertyValues {
  private final List<PropertyValue> propertyValues;

  public DefaultPropertyValues() {
    propertyValues = new ArrayList<>();
  }

  @Override
  public List<PropertyValue> getPropertyValues() {
    return Collections.unmodifiableList(propertyValues);
  }

  @Override
  public void addPropertyValue(PropertyValue pv) {
    Objects.requireNonNull(pv);
    propertyValues.add(pv);
  }

  @Override
  public void merge(PropertyValues other) {
    Objects.requireNonNull(other);
    if (other instanceof DefaultPropertyValues) {
      propertyValues.addAll(((DefaultPropertyValues) other).propertyValues);
    }
  }
}