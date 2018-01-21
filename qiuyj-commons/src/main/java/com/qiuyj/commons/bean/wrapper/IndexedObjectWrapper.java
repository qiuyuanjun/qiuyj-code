package com.qiuyj.commons.bean.wrapper;

import com.qiuyj.commons.bean.ConfigurablePropertyAccessor;

/**
 * @author qiuyj
 * @since 2018/1/19
 */
public interface IndexedObjectWrapper extends ConfigurablePropertyAccessor {

  Class<?> getIndexedPropertyValueType();
}