package com.qiuyj.commons.validate.annotation;

import com.qiuyj.commons.validate.ValidatorBaseAnnotation;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-05-30
 */
public class AnnotationBasedCompositeValidationRule extends AnnotationBasedValidationRule<AnnotationBasedCompositeValidationRule.CompositeValidationRuleAnnotation> {

  private final List<AnnotationBasedValidationRule> delegate;

  private ThreadLocal<List<Annotation>> localErrorAnnotations =
      ThreadLocal.withInitial(ArrayList::new);

  public AnnotationBasedCompositeValidationRule(List<AnnotationBasedValidationRule> delegate) {
    super(CompositeValidationRuleAnnotationImpl.INSTANCE, false);
    this.delegate = Objects.requireNonNull(delegate);
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, CompositeValidationRuleAnnotation annotationContext) {
    resetLocalErrorAnnotations();
    for (AnnotationBasedValidationRule rule : delegate) {
      if (!rule.matchAny(value)) {
        checkAndSetErrorAnnotation(rule);
        return false;
      }
    }
    return true;
  }

  private void resetLocalErrorAnnotations() {
    List<Annotation> errorAnnotations = localErrorAnnotations.get();
    if (!errorAnnotations.isEmpty()) {
      errorAnnotations.clear();
    }
  }

  private void checkAndSetErrorAnnotation(AnnotationBasedValidationRule rule) {
    Annotation annotation = rule.getAnnotationInstance().getAnnotation();
    if (annotation instanceof CompositeAnnotationInstance.CompositeAnnotationImpl) {
      throw new IllegalStateException("Composite's sub item of validation rule cannot be a composite annotation");
    }
    else {
      // 记录错误信息
      List<Annotation> errorAnnotations = localErrorAnnotations.get();
      errorAnnotations.add(annotation);
    }
  }

  @Override
  public AnnotationInstance getAnnotationInstance() {
    List<Annotation> annotations = localErrorAnnotations.get();
    if (annotations.isEmpty()) {
      throw new IllegalStateException("Has no errors, can not invoke this method");
    }
    else {
      return new CompositeAnnotationInstance(annotations);
    }
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
