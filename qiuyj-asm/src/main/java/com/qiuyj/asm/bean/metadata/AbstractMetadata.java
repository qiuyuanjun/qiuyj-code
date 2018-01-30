package com.qiuyj.asm.bean.metadata;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public abstract class AbstractMetadata {

  /**
   * 当前bean的所有注解信息，如果没有（是java.lang.annotation下的注解），那么应当标志null
   */
  protected Map<String, AnnotationMetadata> annotations;

  public void addAnnotationInternal(String annotationName, AnnotationMetadata annotationMetadata) {
    if (Objects.isNull(annotations)) {
      annotations = new LinkedHashMap<>();
    }
    annotations.put(annotationName, annotationMetadata);
  }
}