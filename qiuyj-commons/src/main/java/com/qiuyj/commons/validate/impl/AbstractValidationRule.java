package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.validate.ValidationErrorReport;
import com.qiuyj.commons.validate.ValidationRule;

/**
 * @author qiuyj
 * @since 2018-06-08
 */
public abstract class AbstractValidationRule implements ValidationRule {

  /**
   * 本地错误报告，当前线程可见
   */
  private final ThreadLocal<ValidationErrorReport> localErrorReport =
      ThreadLocal.withInitial(() -> ValidationErrorReport.HAS_NO_ERROR);

  @Override
  public boolean matchAny(Object value) {
    resetLocalErrorReport(); // 重置本地错误报告
    return doMatchAny(value);
  }

  /**
   * 具体的匹配工作，交给子类去实现
   * @param value 要匹配的值
   * @return 如果匹配，返回{@code true}，否则返回{@code false}
   * @see ValidationRule#matchAny(Object)
   */
  protected abstract boolean doMatchAny(Object value);

  @Override
  public boolean matchAll(Object value) {
    resetLocalErrorReport(); // 重置本地错误报告
    return false;
  }

  /**
   * 具体的匹配工作，交给子类去实现
   * @param value 要匹配的值
   * @return 如果匹配，返回{@code true}，否则返回{@code false}
   * @see ValidationRule#matchAll(Object)
   */
  protected abstract boolean doMatchAll(Object value);

  /**
   * 得到本地错误报告
   * @apiNote 该方法必须是验证失败之后才能调用，否则会抛出异常
   * @return 验证之后的错误报告
   */
  protected ValidationErrorReport getLocalValidationErrorReport() {
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

  protected void setLocalErrorReport(ValidationErrorReport validationErrorReport) {
    localErrorReport.set(validationErrorReport);
  }

  private void resetLocalErrorReport() {
    if (localErrorReport.get() != ValidationErrorReport.HAS_NO_ERROR) {
      localErrorReport.set(ValidationErrorReport.HAS_NO_ERROR);
    }
  }
}
