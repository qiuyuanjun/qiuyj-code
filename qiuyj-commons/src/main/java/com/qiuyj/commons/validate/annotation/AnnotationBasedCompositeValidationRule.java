package com.qiuyj.commons.validate.annotation;

import com.qiuyj.commons.validate.ValidatorBaseAnnotation;

import java.lang.annotation.*;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-05-30
 */
public class AnnotationBasedCompositeValidationRule extends AnnotationBasedValidationRule<AnnotationBasedCompositeValidationRule.CompositeValidationRuleAnnotation> {

  private final List<AnnotationBasedValidationRule> delegate;

  public AnnotationBasedCompositeValidationRule(List<AnnotationBasedValidationRule> delegate) {
    super(CompositeValidationRuleAnnotationImpl.INSTANCE, false);
    this.delegate = Objects.requireNonNull(delegate);
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, CompositeValidationRuleAnnotation annotationContext) {
    for (AnnotationBasedValidationRule rule : delegate) {
      if (!rule.matchAny(value)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public AnnotationInstance getAnnotationInstance() {
    CompositeAnnotationInstance annotationInstance = new CompositeAnnotationInstance(null);
    return annotationInstance;
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @ValidatorBaseAnnotation
  @interface CompositeValidationRuleAnnotation {
  }

  public enum CompositeValidationRuleAnnotationImpl implements CompositeValidationRuleAnnotation {
    INSTANCE;

    @Override
    public Class<CompositeValidationRuleAnnotation> annotationType() {
      return CompositeValidationRuleAnnotation.class;
    }
  }
}
