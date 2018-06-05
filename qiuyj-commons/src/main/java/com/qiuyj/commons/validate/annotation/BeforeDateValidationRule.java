package com.qiuyj.commons.validate.annotation;

import java.util.Objects;

import static com.qiuyj.commons.validate.annotation.AfterDateValidationRule.validateDate;

/**
 * @author qiuyj
 * @since 2018-06-05
 */
public class BeforeDateValidationRule extends AnnotationBasedValidationRule<BeforeDate> {

  public BeforeDateValidationRule(BeforeDate annotation) {
    super(annotation, !annotation.nullable());
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, BeforeDate annotationContext) {
    // 如果允许为null，并且值确实为null，那么直接验证通过
    if (annotationContext.nullable() && Objects.isNull(value)) {
      return true;
    }
    else {
      return validateDate(false, value, annotationContext);
    }
  }
}
