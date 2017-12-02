package com.qiuyj.tools.mybatis;

/**
 * @author qiuyj
 * @since 2017/12/2
 */
public class AttributeColumnValueMapping {
  private String attribute;
  private String aliase;
  private Object value;

  public AttributeColumnValueMapping() {}

  public AttributeColumnValueMapping(String attribute, String aliase, Object value) {
    this.attribute = attribute;
    this.aliase = aliase;
    this.value = value;
  }

  @Override
  public String toString() {
    return aliase + " = #{" + attribute + "}";
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public String getAliase() {
    return aliase;
  }

  public void setAliase(String aliase) {
    this.aliase = aliase;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}