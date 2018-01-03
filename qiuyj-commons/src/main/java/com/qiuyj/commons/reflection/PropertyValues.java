package com.qiuyj.commons.reflection;

import java.util.List;

/**
 * 属性-值的包装对象的集合
 * @author qiuyj
 * @since 2018/1/3
 */
public interface PropertyValues {

  /**
   * 得到所有的属性-值包装对象（返回一个不可写的List集合）
   */
  List<PropertyValue> getPropertyValues();

  /**
   * 像这个集合中增加属性-值包装对象
   */
  void addPropertyValue(PropertyValue pv);

  /**
   * 合并另外一个属性-值包装对象的集合
   */
  void merge(PropertyValues other);
}