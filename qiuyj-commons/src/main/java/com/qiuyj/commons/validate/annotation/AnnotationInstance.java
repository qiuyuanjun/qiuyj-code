package com.qiuyj.commons.validate.annotation;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-06-01
 */
public class AnnotationInstance {

  private Annotation annotation;

  public AnnotationInstance(Annotation annotation) {
    if (annotation == AnnotationBasedCompositeValidationRule.CompositeValidationRuleAnnotationImpl.INSTANCE) {
      throw new IllegalArgumentException("");
    }
    else {
      this.annotation = Objects.requireNonNull(annotation);
    }
  }

  // for CompositeAnnotationInstance constructor
  AnnotationInstance() {}

  public Annotation getAnnotation() {
    return annotation;
  }
}
