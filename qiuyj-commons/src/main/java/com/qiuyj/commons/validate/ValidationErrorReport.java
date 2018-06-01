package com.qiuyj.commons.validate;

/**
 * @author qiuyj
 * @since 2018-05-31
 */
public interface ValidationErrorReport {

  /**
   * 验证通过，默认
   */
  ValidationErrorReport HAS_NO_ERROR = new ValidationErrorReport() {

    @Override
    public String toString() {
      return "HAS_NO_ERROR";
    }
  };

  @Override
  String toString();
}
