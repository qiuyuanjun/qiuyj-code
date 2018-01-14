package com.qiuyj.commons.bean;

/**
 * 属性访问器
 * @author qiuyj
 * @since 2018/1/3
 */
public interface PropertyAccessor {

  /**
   * 内嵌属性分割符
   * 属性表达式形如abc.xyz
   */
  String NESTED_PROPERTY_SEPARATOR = ".";

  /**
   * 属性的索引，主要用于数组的访问，集合的访问，map的访问
   * 属性表达式形如
   */
  String INDEXED_PROPERTY_PREFIX = "[";
  String INDEXED_RPOPERTY_SUFFIX = "]";

  /**
   * 设置对应属性的值
   * @param property 要设置值的属性名称
   * @param value 属性值
   */
  void setProperty(String property, Object value);

  /**
   * 设置对应属性的值（可能需要转换类型）
   * @param property 要设置值的属性名称
   * @param strValue 属性值（字符串形式，需要进一步转换）
   */
  void setProperty(String property, String strValue);

  /**
   * 得到对应的属性值
   * @param property 属性名称
   * @return 属性值
   */
  Object getProperty(String property);

  /**
   * 得到对应的属性值作为字符串的形式返回
   * @param property 属性名称
   * @return 字符串形式的属性值
   */
  String getPropertyAsString(String property);
}