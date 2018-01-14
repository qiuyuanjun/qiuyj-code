package com.qiuyj.commons.bean;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/9
 */
public class IndexedProperty extends NestedProperty {

  private String indexedPropertyName;

  public IndexedProperty(PropertyAccessor root, String indexedPropertyName) {
    super(root, null);
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
   * 由于IndexedProperty不做Map的key，所以这里的hashCode方法不是必须的
   */
  @Override
  public int hashCode() {
    return Objects.hash(indexedPropertyName);
  }

}