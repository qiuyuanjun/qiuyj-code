package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.validate.ValidationErrorReport;

import java.util.List;
import java.util.Map;

/**
 * @author qiuyj
 * @since 2018-06-08
 */
public class MapValidationErrorReport implements ValidationErrorReport {

  private final Map<String, ?> map;

  private final List<String> validateFailedKeys;

  public MapValidationErrorReport(Map<String, ?> map, List<String> validateFailedKeys) {
    this.map = map;
    this.validateFailedKeys = validateFailedKeys;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Errors in ");
    Class<? extends Map> mapClass = map.getClass();
    if (!mapClass.isInterface()) {
      sb.append(mapClass);
    }
    sb.append("with values ")
        .append(map)
        .append(".\nBut the required field ")
        .append(validateFailedKeys)
        .append(" is not present.");
    return sb.toString();
  }

  @Override
  public void forEachError(ErrorConsumer errorConsumer) {
    MapValidationErrorConsumer consumer = MapValidationErrorConsumer.class.cast(errorConsumer);
    validateFailedKeys.forEach(consumer::consume);
  }

  /**
   * 专门针对{@code MapValidation}的错误信息消费回调接口
   */
  @FunctionalInterface
  public interface MapValidationErrorConsumer extends ErrorConsumer {

    /**
     * 消费对应的错误字段和注解
     */
    void consume(String validateFailedKey);
  }
}
