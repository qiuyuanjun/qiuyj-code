package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.validate.ValidationErrorReport;
import com.qiuyj.commons.validate.ValidationResult;

import java.util.Objects;

/**
 * 默认实现
 * @author qiuyj
 * @since 2018-06-06
 */
public class DefaultValidationResult implements ValidationResult {

  private final ValidationErrorReport errorReport;

  public DefaultValidationResult(ValidationErrorReport errorReport) {
    this.errorReport = Objects.requireNonNull(errorReport);
  }

  @Override
  public ValidationErrorReport getReport() {
    return errorReport;
  }
}
