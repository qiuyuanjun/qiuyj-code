package com.qiuyj.commons.bean.exception;

/**
 * 当内嵌属性是null，且不支持内嵌属性实例化的时候抛出该异常
 * @author qiuyj
 * @since 2018/1/18
 */
public class NestedValueIsNullException extends RuntimeException {

  public NestedValueIsNullException(Object obj, String nestedPropertyName) {
    super("Nested property: " + nestedPropertyName + " of class: " + obj.getClass() + " is null");
  }
}