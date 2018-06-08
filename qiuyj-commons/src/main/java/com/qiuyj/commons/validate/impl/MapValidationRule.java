package com.qiuyj.commons.validate.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-06-07
 */
class MapValidationRule extends AbstractValidationRule {

  /**
   * map中需要验证的key
   */
  private final List<String> validateKeys;

  public MapValidationRule(List<String> validateKeys) {
    this.validateKeys = validateKeys;
  }

  @Override
  protected boolean doMatchAny(Object value) {
    Map<String, ?> mapValue = castToMap(value);
    for (String validateKey : validateKeys) {
      // 目前只支持null检查
      if (Objects.isNull(mapValue.get(validateKey))) {
        // 生成错误报告
        setLocalErrorReport(createErrorReport(mapValue, List.of(validateKey)));
        return false;
      }
    }
    return true;
  }

  @Override
  protected boolean doMatchAll(Object value) {
    Map<String, ?> mapValue = castToMap(value);
    List<String> validateFailedKeys = new ArrayList<>();
    for (String validateKey : validateKeys) {
      // 目前只支持null检查
      if (Objects.isNull(mapValue.get(validateKey))) {
        // 记录所有没有验证通过的key
        validateFailedKeys.add(validateKey);
      }
    }
    if (validateFailedKeys.isEmpty()) {
      return true;
    }
    else {
      setLocalErrorReport(createErrorReport(mapValue, validateFailedKeys));
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<String, ?> castToMap(Object value) {
    return Map.class.cast(value);
  }

  /**
   * 创建错误报告
   * @param map 对验证的map对象
   * @param key 没有验证通过的键
   * @return {@code MapValidationErrorReport}
   */
  private MapValidationErrorReport createErrorReport(Map<String, ?> map, List<String> key) {
    return new MapValidationErrorReport(map, key);
  }

}
