package com.qiuyj.commons.bean;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public interface IndexedObjectWrapper extends PropertyAccessor {

  /**
   * 得到Map或者List或者数组实际存储的值的类型
   */
  Class<?> getIndexedPropertyValueType();
}