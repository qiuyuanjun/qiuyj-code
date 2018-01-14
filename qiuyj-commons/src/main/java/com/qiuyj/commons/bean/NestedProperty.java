package com.qiuyj.commons.bean;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public class NestedProperty {

  private final PropertyAccessor currentObject;

  private String nestedPropertyName;

  public NestedProperty(PropertyAccessor currentObject, String nestedPropertyName) {
    this.currentObject = currentObject;
    this.nestedPropertyName = nestedPropertyName;
  }

  public PropertyAccessor getCurrentObject() {
    return currentObject;
  }

  public String getNestedPropertyName() {
    return nestedPropertyName;
  }

  public void setNestedPropertyName(String nestedPropertyName) {
    this.nestedPropertyName = nestedPropertyName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NestedProperty that = (NestedProperty) o;
    return Objects.equals(currentObject, that.currentObject) &&
        Objects.equals(nestedPropertyName, that.nestedPropertyName);
  }

  /**
   * 可以不用重写hashCode方法，因为NestedProperty不会作为Map的key
   */
  @Override
  public int hashCode() {
    return Objects.hash(currentObject, nestedPropertyName);
  }
}