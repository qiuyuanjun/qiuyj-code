package com.qiuyj.commons.reflection;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/7
 */
public class NestedProperty {

  private final PropertyAccessor root;

  private String nestedPropertyName;

  public NestedProperty(PropertyAccessor root, String nestedPropertyName) {
    this.root = root;
    this.nestedPropertyName = nestedPropertyName;
  }

  public PropertyAccessor getRoot() {
    return root;
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
    return Objects.equals(root, that.root) && Objects.equals(nestedPropertyName, that.nestedPropertyName);
  }

  /**
   * 由于NestedProperty不做Map的key，所以这里的hashCode方法不是必须的
   */
  @Override
  public int hashCode() {
    return Objects.hash(nestedPropertyName);
  }
}