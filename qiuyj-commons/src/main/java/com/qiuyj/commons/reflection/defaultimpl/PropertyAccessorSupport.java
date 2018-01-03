package com.qiuyj.commons.reflection.defaultimpl;

import com.qiuyj.commons.reflection.PropertyAccessor;
import com.qiuyj.commons.reflection.PropertyValue;
import com.qiuyj.commons.reflection.PropertyValues;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author qiuyj
 * @since 2018/1/3
 */
public class PropertyAccessorSupport implements PropertyAccessor {
  private final Map<String, Object> propertyValues;

  public PropertyAccessorSupport() {
    propertyValues = new HashMap<>();
  }

  @Override
  public void add(PropertyValue pv) {
    Objects.requireNonNull(pv);
    if (Objects.isNull(pv.getProperty())) {
      throw new IllegalArgumentException("'property' can not be null");
    }
    else if (Objects.isNull(pv.getValue())) {
      propertyValues.remove(pv.getProperty());
    }
    else {
      propertyValues.put(pv.getProperty(), pv.getValue());
    }
  }

  @Override
  public void addAll(PropertyValues pvs) {
    Objects.requireNonNull(pvs);
    for (PropertyValue pv : pvs.getPropertyValues()) {
      add(pv);
    }
  }

  @Override
  public Object getValue(String property) {
    Objects.requireNonNull(property, "'property' can not be null");
    return propertyValues.get(property);
  }

  @Override
  public void addValue(String property, Object value) {
    add(new ImmutablePropertyValue(property, value));
  }

  @Override
  public boolean remove(PropertyValue pv) {
    Objects.requireNonNull(pv);
    Object currObj = getValue(pv.getProperty());
    boolean remove = false;
    if (Objects.equals(currObj, pv.getValue())) {
      // 如果值一样，那么就删除
      remove = removeValue(pv.getProperty());
    }
    return remove;
  }

  @Override
  public boolean removeValue(String property) {
    Objects.requireNonNull(property);
    return Objects.nonNull(propertyValues.remove(property));
  }

  @Override
  public boolean hasProperty(String property) {
    return !Objects.isNull(property) && propertyValues.containsKey(property);
  }

  @Override
  public String[] properties() {
    Set<String> keySet = propertyValues.keySet();
    return keySet.toArray(new String[keySet.size()]);
  }
}