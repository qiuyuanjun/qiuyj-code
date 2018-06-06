package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.validate.ValidationErrorReport;
import com.qiuyj.commons.validate.ValidationResult;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-06-06
 */
public class BeanValidationResult implements ValidationResult {

  private final BeanValidationErrorReport errorReport;

  public BeanValidationResult(BeanValidationErrorReport errorReport) {
    this.errorReport = Objects.requireNonNull(errorReport);
  }

  @Override
  public ValidationErrorReport getReport() {
    return errorReport;
  }
}
