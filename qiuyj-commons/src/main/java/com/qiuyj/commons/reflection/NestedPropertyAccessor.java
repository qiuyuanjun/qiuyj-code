package com.qiuyj.commons.reflection;

/**
 * @author qiuyj
 * @since 2018/1/5
 */
public abstract class NestedPropertyAccessor extends PropertyAccessorSupport {

  private boolean autoInstantiateNestedPropertyNullValue;

  private PropertyAccessor root;

  protected NestedPropertyAccessor(PropertyAccessor root) {
    autoInstantiateNestedPropertyNullValue = true;
    this.root = root;
  }

  @Override
  public void setAutoInstantiateNestedPropertyNullValue(boolean autoInstantiateNestedPropertyNullValue) {
    this.autoInstantiateNestedPropertyNullValue = autoInstantiateNestedPropertyNullValue;
  }
}