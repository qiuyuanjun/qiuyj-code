package com.qiuyj.commons.bean;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public class IndexedProperty extends NestedProperty {

  private String indexedPropertyName;

  public IndexedProperty(PropertyAccessor currentObject, String indexedPropertyName) {
    super(currentObject, null);
    this.indexedPropertyName = indexedPropertyName;
  }

  public String getIndexedPropertyName() {
    return indexedPropertyName;
  }

  public void setIndexedPropertyName(String indexedPropertyName) {
    this.indexedPropertyName = indexedPropertyName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    IndexedProperty that = (IndexedProperty) o;
    return Objects.equals(indexedPropertyName, that.indexedPropertyName);
  }

  /**
   * 可以不用重写hashCode方法，因为IndexedProperty不会作为Map的key
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), indexedPropertyName);
  }
}