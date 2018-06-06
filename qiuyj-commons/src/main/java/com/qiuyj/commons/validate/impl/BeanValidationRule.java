package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.AnnotationUtils;
import com.qiuyj.commons.ClassUtils;
import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.validate.ValidationErrorReport;
import com.qiuyj.commons.validate.ValidationRule;
import com.qiuyj.commons.validate.ValidatorBaseAnnotation;
import com.qiuyj.commons.validate.annotation.AnnotationBasedCompositeValidationRule;
import com.qiuyj.commons.validate.annotation.AnnotationBasedValidationRule;
import com.qiuyj.commons.validate.annotation.AnnotationInstance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author qiuyj
 * @since 2018-05-29
 */
public class BeanValidationRule implements ValidationRule {

  private static final String ANNOTATION_VALIDATIONRULE_SUFFIX = "ValidationRule";

  /**
   * 当前bean所有所需要验证的字段{@code Field}所对应的{@code AnnotationBasedValidationRule}的映射map
   */
  private Map<Field, AnnotationBasedValidationRule> fieldAnnotationBasedValidationRules;

  /**
   * 错误报告
   */
  private final ThreadLocal<ValidationErrorReport> localErrorReport =
      ThreadLocal.withInitial(() -> ValidationErrorReport.HAS_NO_ERROR);

  /**
   * 要验证的{@code Class}对象
   */
  private final Class<?> beanClass;

  public BeanValidationRule(Class<?> beanClass) {
    this.beanClass = Objects.requireNonNull(beanClass);
    this.fieldAnnotationBasedValidationRules = parseBeanClass(beanClass);
  }

  @Override
  public boolean matchAny(Object value) {
    // 如果当前的bean没有任何注解，那么直接验证通过
    if (Objects.isNull(fieldAnnotationBasedValidationRules) || fieldAnnotationBasedValidationRules.isEmpty()) {
      return true;
    }
    else if (Objects.nonNull(value)) {
      resetLocalErrorReport();  // 重置本地错误报告
      for (Map.Entry<Field, AnnotationBasedValidationRule> me : fieldAnnotationBasedValidationRules.entrySet()) {
        Field field = me.getKey();
        if (!field.canAccess(value)) {
          if (!field.trySetAccessible()) {
            throw new IllegalStateException("Can not access field: " + field.getName() + " in " + value.getClass());
          }
        }
        Object fieldValue = null;
        try {
          fieldValue = field.get(value);
        }
        catch (IllegalAccessException e) {
          // ignore, never get here
        }
        if (!me.getValue().matchAny(fieldValue)) {
          createAndSetErrorReport(field, me.getValue());
          return false;
        }
      }
      return true;
    }
    else {
      throw new IllegalStateException("The object to be verified is null");
    }
  }

  @Override
  public boolean matchAll(Object value) {
    // 如果当前的bean没有任何注解，那么直接验证通过
    if (Objects.isNull(fieldAnnotationBasedValidationRules) || fieldAnnotationBasedValidationRules.isEmpty()) {
      return true;
    }
    else if (Objects.nonNull(value)) {
      resetLocalErrorReport();  // 重置本地错误报告
      Map<Field, AnnotationBasedValidationRule> validateFailed = new HashMap<>();
      for (Map.Entry<Field, AnnotationBasedValidationRule> me : fieldAnnotationBasedValidationRules.entrySet()) {
        Field field = me.getKey();
        if (!field.canAccess(value)) {
          if (!field.trySetAccessible()) {
            throw new IllegalStateException("Can not access field: " + field.getName() + " in " + value.getClass());
          }
        }
        Object fieldValue = null;
        try {
          fieldValue = field.get(value);
        }
        catch (IllegalAccessException e) {
          // ignore, never get here
        }
        if (!me.getValue().matchAll(fieldValue)) {
          validateFailed.put(field, me.getValue());
        }
      }
      if (validateFailed.isEmpty()) {
        return true;
      }
      else {
        // 生成验证结果
        createAndSetErrorReport(validateFailed);
        return false;
      }
    }
    else {
      throw new IllegalStateException("The object to be verified is null");
    }
  }

  private void resetLocalErrorReport() {
    if (localErrorReport.get() != ValidationErrorReport.HAS_NO_ERROR) {
      localErrorReport.set(ValidationErrorReport.HAS_NO_ERROR);
    }
  }

  private void createAndSetErrorReport(Field field, AnnotationBasedValidationRule validationRule) {
    if (field.getDeclaringClass() != beanClass) {
      throw new IllegalStateException("Illegal field: " + field.getName() + ", actual belong to " + field.getDeclaringClass() + ", but nedded " + beanClass);
    }
    else {
      BeanValidationErrorReport errorReport = new BeanValidationErrorReport(beanClass);
      // 对errorReport做初始化
      // annotationInstance可能是CompositeAnnotationInstance
      AnnotationInstance annotationInstance = validationRule.getAnnotationInstance();
      errorReport.registerErrorField(field, annotationInstance.getAnnotation());
      localErrorReport.set(errorReport);
    }
  }

  private void createAndSetErrorReport(Map<Field, AnnotationBasedValidationRule> validateFailed) {
    BeanValidationErrorReport errorReport = new BeanValidationErrorReport(beanClass);
    validateFailed.forEach((field, validationRule) -> {
      if (field.getDeclaringClass() != beanClass) {
        throw new IllegalStateException("Illegal field: " + field.getName() + ", actual belong to " + field.getDeclaringClass() + ", but nedded " + beanClass);
      }
      else {
        // 对errorReport做初始化
        // annotationInstance可能是CompositeAnnotationInstance
        AnnotationInstance annotationInstance = validationRule.getAnnotationInstance();
        errorReport.registerErrorField(field, annotationInstance.getAnnotation());
      }
    });
    localErrorReport.set(errorReport);
  }

  private static Map<Field, AnnotationBasedValidationRule> parseBeanClass(Class<?> beanClass) {
    Field[] fields = ClassUtils.getAllDeclaredFields(beanClass);
    if (fields.length > 0) {
      Map<Field, AnnotationBasedValidationRule> beanValidationRules = new HashMap<>(fields.length);
      for (Field field : fields) {
        List<? extends Annotation> targetAnnotations = AnnotationUtils.findAnnotationAnnotatedBy(field, ValidatorBaseAnnotation.class);
        if (targetAnnotations.size() > 1) {
          List<AnnotationBasedValidationRule> rules = new ArrayList<>(targetAnnotations.size());
          for (Annotation anno : targetAnnotations) {
            rules.add(getAnnotationBasedValidationRule(anno));
          }
          beanValidationRules.put(field, new AnnotationBasedCompositeValidationRule(rules));
        }
        else if (targetAnnotations.size() == 1) {
          Annotation validatorAnnotation = targetAnnotations.get(0);
          beanValidationRules.put(field, getAnnotationBasedValidationRule(validatorAnnotation));
        }
      }
      return beanValidationRules;
    }
    else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static AnnotationBasedValidationRule getAnnotationBasedValidationRule(Annotation validatorAnnotation) {
    Class<? extends Annotation> annotationType = validatorAnnotation.annotationType();
    String validationRuleName = annotationType.getName() + ANNOTATION_VALIDATIONRULE_SUFFIX;
    Class<? extends AnnotationBasedValidationRule> cls;
    try {
      cls = (Class<? extends AnnotationBasedValidationRule>) ClassUtils.classForName(validationRuleName, BeanValidationRule.class.getClassLoader());
    }
    catch (ClassNotFoundException e) {
      throw new IllegalStateException("Unable to find custom validation rule class, " +
          "custom validation rule class's name is expected to be named as an pattern of {AnnotationName + ValidationRule}");
    }
    return ReflectionUtils.instantiateClass(cls, new Object[] {validatorAnnotation}, new Class[] {annotationType});
  }

  ValidationErrorReport getLocalValidationErrorReport() {
    ValidationErrorReport errorReport = localErrorReport.get();
    // 如果当前本地错误报告是ValidationErrorReport.HAS_NO_ERROR
    // 那么抛出异常，无法获取
    if (errorReport == ValidationErrorReport.HAS_NO_ERROR) {
      throw new IllegalStateException("HAS NO ERROR");
    }
    else {
      return errorReport;
    }
  }
}
