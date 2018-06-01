package com.qiuyj.commons.validate.annotation;

import java.lang.annotation.Annotation;

/**
 * @author qiuyj
 * @since 2018-06-01
 */
public class AnnotationInstance {

  private final Annotation annotation;

  public AnnotationInstance(Annotation annotation) {
    if (annotation == AnnotationBasedCompositeValidationRule.CompositeValidationRuleAnnotationImpl.INSTANCE) {
      throw new IllegalArgumentException("");
    }
    else {
      this.annotation = annotation;
    }
  }

  public Annotation getAnnotation() {
    return annotation;
  }
}
