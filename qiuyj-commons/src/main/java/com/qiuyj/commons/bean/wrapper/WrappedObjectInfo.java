package com.qiuyj.commons.bean.wrapper;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public interface WrappedObjectInfo<T> {

  /**
   * 得到被包装的对象的Class对象
   */
  Class<T> getWrappedClass();

  /**
   * 得到被包装的对象
   */
  T getWrappedInstance();
}
