package com.qiuyj.commons.reflection.defaultimpl;

import com.qiuyj.commons.reflection.PropertyValue;

/**
 * 不可变的属性-值包装对象（即property和value只能通过构造器注入，一旦注入，便不能在修改）
 * @author qiuyj
 * @since 2018/1/3
 */
public class ImmutablePropertyValue implements PropertyValue {
  private final String property;
  private final Object value;

  public ImmutablePropertyValue() {
    this(null, null);
  }

  public ImmutablePropertyValue(String property) {
    this(property, null);
  }

  public ImmutablePropertyValue(String property, Object value) {
    this.property = property;
    this.value = value;
  }

  @Override
  public String getProperty() {
    return property;
  }

  @Override
  public Object getValue() {
    return value;
  }
}