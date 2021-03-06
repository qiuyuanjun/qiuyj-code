package com.qiuyj.commons.validate.annotation;

/**
 * @author qiuyj
 * @since 2018-05-30
 */
public class MinLengthValidationRule extends AnnotationBasedValidationRule<MinLength> {

  public MinLengthValidationRule(MinLength minLength) {
    super(minLength, true);
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, MinLength annotationInstance) {
    int minLength = annotationInstance.value();
    if (minLength < 0) {
      throw new IllegalArgumentException("@MinLength's value can not be negative");
    }
    else {
      return ((CharSequence) value).length() > minLength;
    }
  }

  @Override
  protected void checkValueType(Object value) {
    if (!(value instanceof CharSequence)) {
      throw new IllegalStateException("@MinLength must be annotated on an CharSequence type or subclass type property");
    }
  }
}
