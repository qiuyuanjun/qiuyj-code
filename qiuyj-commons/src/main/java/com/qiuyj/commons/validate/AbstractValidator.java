package com.qiuyj.commons.validate;

import com.qiuyj.commons.validate.impl.DefaultValidationResult;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-05-29
 */
public abstract class AbstractValidator<T> implements Validator<T> {

  /**
   * 验证规则，不同的子类有不同的验证规则
   */
  private final ValidationRule rule;

  protected AbstractValidator(ValidationRule rule) {
    this.rule = Objects.requireNonNull(rule);
  }

  @Override
  public void validateWithException(T validatedObject) throws ValidationException {
    checkValidatedObjectType(validatedObject);
    if (!rule.matchAny(validatedObject)) {
      // 收集错误报告
      ValidationErrorReport errorReport = doGetValidationErrorReport();
      throw new ValidationException(errorReport);
    }
  }

  @Override
  public ValidationResult validateWithResult(T validatedObject) {
    checkValidatedObjectType(validatedObject);
    if (!rule.matchAll(validatedObject)) {
      // 收集错误报告
      ValidationErrorReport errorReport = doGetValidationErrorReport();
      // 构建验证结果对象
      return doCreateValidationResult(errorReport);
    }
    else {
      return ValidationResult.VALIDATE_SUCCESSFUL_VALIDATION_RESULT;
    }
  }

  /*
   * 得到对应的验证规则，提供给子类使用（TODO 是否需要public权限？）
   */
  protected ValidationRule getValidationRule() {
    return rule;
  }

  /**
   * 检测所需要的类型是否和实际类型是否一致，如果不一致，抛出异常
   * @param validatedObject 被检测的对象
   */
  private void checkValidatedObjectType(T validatedObject) {
    if (!isInstanceTypeEquals(validatedObject)) {
      throw new IllegalArgumentException("The type needed is not in conformity with the actual type");
    }
  }

  /**
   * 根据子类自己的规则判断传入的参数是否和所需要的参数类型一致
   * @param validatedObject 要判断的对象
   * @return 如果类型一致，返回{@code true}，否则返回{@code false}
   */
  protected abstract boolean isInstanceTypeEquals(T validatedObject);

  /**
   * 通过子类获取对应的配套的错误报告
   * @return 错误报告对象
   */
  protected abstract ValidationErrorReport doGetValidationErrorReport();

  /**
   * 创建对应的验证结果，不同的实现类有不同的验证结果
   */
  protected ValidationResult doCreateValidationResult(ValidationErrorReport errorReport) {
    return new DefaultValidationResult(errorReport);
  }
}
