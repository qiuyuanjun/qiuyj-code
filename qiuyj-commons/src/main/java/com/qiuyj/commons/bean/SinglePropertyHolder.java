package com.qiuyj.commons.bean;

/**
 * @author qiuyj
 * @since 2018/1/18
 */
class SinglePropertyHolder {

  private final String currentProperty;

  private final String nestedPropertyPath;

  private final String indexedProperty;

  public SinglePropertyHolder(String currentProperty, String nestedPropertyPath, String indexedProperty) {
    this.currentProperty = currentProperty;
    this.nestedPropertyPath = nestedPropertyPath;
    this.indexedProperty = indexedProperty;
  }

  public String getCurrentProperty() {
    return currentProperty;
  }

  public String getNestedPropertyPath() {
    return nestedPropertyPath;
  }

  public String getIndexedProperty() {
    return indexedProperty;
  }

}