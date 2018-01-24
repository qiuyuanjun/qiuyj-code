package com.qiuyj.commons.bean.parse.metadata;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class BeanMetadata extends AbstractMetadata {

  /**
   * 当前bean的父类，如果没有（Object）那么，此属性应当标志null
   */
  private BeanMetadata parent;

  /**
   * 当前bean的所有自己声明的set和get方法
   */
  private MethodMetadata[] methods;

  /**
   * 当前bean的所有自己声明的非static final的属性
   */
  private FieldMetadata[] fields;

  /**
   * 当前bean的Class对象
   */
  private final Class<?> beanCls;

  public BeanMetadata(Class<?> beanCls) {
    this.beanCls = Objects.requireNonNull(beanCls);
  }
}