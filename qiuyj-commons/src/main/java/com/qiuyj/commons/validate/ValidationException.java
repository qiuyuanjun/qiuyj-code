package com.qiuyj.commons.validate;

/**
 * @author qiuyj
 * @since 2018-05-29
 */
public class ValidationException extends Exception {

  public ValidationException(ValidationErrorReport errorReport) {
    super(checkTypeAndGetErrorString(errorReport));
  }

  private static String checkTypeAndGetErrorString(ValidationErrorReport errorReport) {
    if (errorReport == ValidationErrorReport.HAS_NO_ERROR) {
      throw new IllegalArgumentException("$HAS_NO_VALIDATION_ERROR$");
    }
    else {
      return errorReport.toString();
    }
  }
}
