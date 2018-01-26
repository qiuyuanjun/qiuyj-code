package com.qiuyj.asm.bean.metadata;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public abstract class AbstractMetadata {

  /**
   * 当前bean的所有泛型，如果没有，那么此属性应当标志为null
   */
  private GenericMetadata[] generics;

  /**
   * 当前bean的所有注解信息，如果没有（是java.lang.annotation下的注解），那么应当标志null
   */
  private AnnotationMetadata[] annotations;
}