package com.qiuyj.commons.validate.annotation;

import com.qiuyj.commons.AnnotationUtils;
import com.qiuyj.commons.validate.ValidationRule;
import com.qiuyj.commons.validate.ValidatorBaseAnnotation;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * 所有基于注解的验证规则都必须继承这个类
 * @author qiuyj
 * @since 2018-05-30
 */
public abstract class AnnotationBasedValidationRule<A extends Annotation> implements ValidationRule, AnnotationInstanceCapable {

  /**
   * 获取错误信息的方法名
   */
  public static final String ERROR_MSG_METHODNAME_IN_ANNOTATION = "message";

  private A annotation;

  private AnnotationInstance annotationInstance;

  private final boolean mustNotNull;

  protected AnnotationBasedValidationRule(A annotation, boolean mustNotNull) {
    if (!AnnotationUtils.hasAnnotation(annotation.annotationType(), ValidatorBaseAnnotation.class)) {
      throw new IllegalStateException("Must be annotated by @ValidatorAnnotation");
    }
    else {
      this.annotation = annotation;
      this.mustNotNull = mustNotNull;
    }
  }

  @Override
  public boolean matchAny(Object value) {
    if (mustNotNull && Objects.isNull(value)) {
      return false;
    }
    else {
      if (Objects.nonNull(value)) {
        checkValueType(value);
      }
      return doMatchAccordingtoAnnotation(value, annotation);
    }
  }

  @Override
  public boolean matchAll(Object value) {
    return false;
  }

  @Override
  public AnnotationInstance getAnnotationInstance() {
    if (Objects.isNull(annotationInstance)) {
      annotationInstance = new AnnotationInstance(annotation);
    }
    return annotationInstance;
  }

  /**
   * 检查值类型是否合法，交给子类实现
   */
  protected void checkValueType(Object value) {
    // for subclass
  }

  /**
   * 通过注解验证规则匹配对应的值
   * @param value 要验证的值
   * @param annotationContext 对应的注解上下文
   * @return {@code true}为匹配成功,匹配失败返回{@code false}
   */
  protected abstract boolean doMatchAccordingtoAnnotation(Object value, A annotationContext);

}
