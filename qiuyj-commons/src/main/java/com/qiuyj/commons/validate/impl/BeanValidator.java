package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.validate.AbstractValidator;
import com.qiuyj.commons.validate.ValidationErrorReport;
import com.qiuyj.commons.validate.ValidationResult;

/**
 * 对javabean的验证器
 * @author qiuyj
 * @since 2018-05-31
 */
public class BeanValidator<T> extends AbstractValidator<T> {

  private final Class<? extends T> beanClass;

  public BeanValidator(Class<? extends T> cls) {
    super(init(cls));
    this.beanClass = cls;
  }

  @Override
  public Class<? extends T> getValidatedClass() {
    return beanClass;
  }

  @Override
  protected boolean isInstanceTypeEquals(T validatedObject) {
    return beanClass.isInstance(validatedObject);
  }

  @Override
  protected ValidationErrorReport doGetValidationErrorReport() {
    BeanValidationRule rule = BeanValidationRule.class.cast(getValidationRule());
    return rule.getLocalValidationErrorReport();
  }

  @Override
  protected ValidationResult doCreateValidationResult(ValidationErrorReport errorReport) {
    return null;
  }

  private static BeanValidationRule init(Class<?> cls) {
    if (cls.isPrimitive()) {
      throw new IllegalArgumentException("BeanValidator not support primitive type");
    }
    else if (cls.isArray()) {
      throw new IllegalArgumentException("BeanValidator not support array type");
    }
    else if (cls.isInterface()) {
      throw new IllegalArgumentException("BeanValidator not support interface type");
    }
    else {
      String className = cls.getName();
      if (className.startsWith("java.")
          || className.startsWith("javax.")
          || className.startsWith("jdk.")
          || className.startsWith("com.sun.")
          || className.startsWith("sun.")) {
        throw new IllegalArgumentException("BeanValidator doesn't accept jdk's class library's type: " + cls.getName());
      }
      else {
        return new BeanValidationRule(cls);
      }
    }
  }
}
