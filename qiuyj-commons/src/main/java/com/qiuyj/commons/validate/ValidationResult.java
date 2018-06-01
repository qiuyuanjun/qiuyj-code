package com.qiuyj.commons.validate;

/**
 * 验证结果
 * @author qiuyj
 * @since 2018-05-29
 */
public interface ValidationResult {

  /*
   * 验证成功的验证结果
   */
  ValidationResult VALIDATE_SUCCESSFUL_VALIDATION_RESULT = new ValidationResult() {

    @Override
    public boolean hasErrors() {
      return false;
    }

    @Override
    public ValidationErrorReport getReport() {
      return ValidationErrorReport.HAS_NO_ERROR;
    }
  };

  /**
   * 是否验证有错误
   * @return 如果有错误，那么返回{@code true}，否则返回{@code false}
   */
  boolean hasErrors();

  /**
   * 得到对应的错误报告
   * @return 对应的错误报告
   */
  ValidationErrorReport getReport();
}
