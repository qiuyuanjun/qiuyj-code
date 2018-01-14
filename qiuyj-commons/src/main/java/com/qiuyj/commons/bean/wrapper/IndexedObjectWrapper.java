package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.ConfigurablePropertyAccessor;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public interface IndexedObjectWrapper<T> extends ConfigurablePropertyAccessor {

  /**
   * 得到Map或者List或者数组实际存储的值的类型
   */
  Class<T> getIndexedPropertyValueType();

}