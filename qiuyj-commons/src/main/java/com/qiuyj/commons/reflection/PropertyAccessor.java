package com.qiuyj.commons.reflection;

/**
 * 属性访问器
 * @author qiuyj
 * @since 2018/1/3
 */
public interface PropertyAccessor {

  /**
   * 添加属性
   */
  void add(PropertyValue pv);

  /**
   * 添加所有的属性
   */
  void addAll(PropertyValues pvs);

  /**
   * 根据对应的属性得到对应的值，如果属性不存在，那么返回null
   * @param property 属性
   * @return 对应的值
   */
  Object getValue(String property);

  /**
   * 添加属性
   * @param property 属性（必须不能为null）
   * @param value 值（如果为null，那么将会移除对应的属性）
   */
  void addValue(String property, Object value);

  /**
   * 移除属性，必须同时满足属性和值都完全相等才会移除
   */
  boolean remove(PropertyValue pv);

  /**
   * 移除属性
   * @param property 要移除的属性
   */
  boolean removeValue(String property);

  /**
   * 是否有对应的属性（一定是不为null的属性）
   * @param property 属性
   */
  boolean hasProperty(String property);

  /**
   * 得到所有不为null的属性
   */
  String[] properties();
}