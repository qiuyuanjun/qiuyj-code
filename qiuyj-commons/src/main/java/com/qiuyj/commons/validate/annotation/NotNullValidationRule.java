package com.qiuyj.commons.validate.annotation;

/**
 * @author qiuyj
 * @since 2018-05-30
 */
public class NotNullValidationRule extends AnnotationBasedValidationRule<NotNull> {

  public NotNullValidationRule(NotNull notNull) {
    super(notNull, true);
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, NotNull annotationContext) {
    // 如果能到达这里，一定是不为null的值，所以这里直接返回true
    return true;
  }
}
