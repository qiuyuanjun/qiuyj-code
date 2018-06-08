package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.StringUtils;
import com.qiuyj.commons.validate.ValidationErrorReport;
import com.qiuyj.commons.validate.annotation.AnnotationBasedValidationRule;
import com.qiuyj.commons.validate.annotation.CompositeAnnotationInstance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author qiuyj
 * @since 2018-06-01
 */
public class BeanValidationErrorReport implements ValidationErrorReport {

  private Map<Field, Annotation> errorFields = new LinkedHashMap<>();

  private final Class<?> beanClass;

  public BeanValidationErrorReport(Class<?> beanClass) {
    this.beanClass = beanClass;
  }

  @Override
  public String toString() {
    StringBuilder subErrors = new StringBuilder();
    StringJoiner errorFieldsJoiner = new StringJoiner(",\n ", ":\n[\n ", "\n]");
    errorFields.forEach((field, annotation) -> {
      subErrors.append("{")
          .append(field.getName())
          .append(":")
          .append(getErrorMessage(annotation))
          .append("}");
      errorFieldsJoiner.add(subErrors.toString());
      subErrors.delete(0, subErrors.length());
    });
    return "Errors in " + beanClass.getName() + errorFieldsJoiner.toString();
  }

  @Override
  public void forEachError(ErrorConsumer errorConsumer) {
    BeanValidationErrorConsumer beanValidationErrorConsumer = BeanValidationErrorConsumer.class.cast(errorConsumer);
    for (Map.Entry<Field, Annotation> me : errorFields.entrySet()) {
      Field errorField = me.getKey();
      List<Annotation> annotations;
      if (me.getValue() instanceof CompositeAnnotationInstance.CompositeAnnotationImpl) {
        annotations = ((CompositeAnnotationInstance.CompositeAnnotationImpl) me.getValue()).compositeAnnotation();
      }
      else {
        annotations = Collections.singletonList(me.getValue());
      }
      beanValidationErrorConsumer.consume(errorField, annotations);
    }
  }

  /**
   * 注册错误字段
   * @param field 错误的字段
   * @param annotation 对应标注的注解
   */
  void registerErrorField(Field field, Annotation annotation) {
    errorFields.put(field, annotation);
  }

  private String getErrorMessage(Annotation annotationInstance) {
    List<Annotation> annotations;
    if (annotationInstance instanceof CompositeAnnotationInstance.CompositeAnnotationImpl) {
      annotations = ((CompositeAnnotationInstance.CompositeAnnotationImpl) annotationInstance).compositeAnnotation();
    }
    else {
      annotations = Collections.singletonList(annotationInstance);
    }
    StringJoiner errorMsgJoiner = new StringJoiner(",");
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      Method errorMsgMethod = null;
      try {
        errorMsgMethod = annotationType.getDeclaredMethod(AnnotationBasedValidationRule.ERROR_MSG_METHODNAME_IN_ANNOTATION);
      }
      catch (NoSuchMethodException e) {
        // ignore
      }
      String errorMsg = null;
      if (Objects.nonNull(errorMsgMethod)) {
        try {
          errorMsg = (String) errorMsgMethod.invoke(annotation);
        } catch (Exception e) {
          // ignore
        }
      }
      if (StringUtils.isBlank(errorMsg)) {
        errorMsg = "does not meet the requirements of the annotation @" + annotationType.getName();
      }
      errorMsgJoiner.add(errorMsg);
    }
    return errorMsgJoiner.toString();
  }

  /**
   * 专门针对{@code BeanValidation}的错误信息消费回调接口
   */
  @FunctionalInterface
  public interface BeanValidationErrorConsumer extends ErrorConsumer {

    /**
     * 消费对应的错误字段和注解
     */
    void consume(Field field, List<Annotation> annotations);
  }
}
