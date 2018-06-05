package com.qiuyj.commons.validate.annotation;

/**
 * @author qiuyj
 * @since 2018-05-30
 */
public class MaxLengthValidationRule extends AnnotationBasedValidationRule<MaxLength> {

  public MaxLengthValidationRule(MaxLength maxLength) {
    super(maxLength, true);
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, MaxLength annotationInstance) {
    int maxLength = annotationInstance.value();
    if (maxLength < 0) {
      throw new IllegalArgumentException("@MaxLength's value can not be negative");
    }
    else {
      return ((CharSequence) value).length() < maxLength;
    }
  }

  @Override
  protected void checkValueType(Object value) {
    if (!(value instanceof CharSequence)) {
      throw new IllegalStateException("@MaxLength must be annotated on an CharSequence type or subclass type property");
    }
  }
}
