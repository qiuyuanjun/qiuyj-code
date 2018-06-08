package com.qiuyj.commons.validate.impl;

import com.qiuyj.commons.validate.AbstractValidator;
import com.qiuyj.commons.validate.ValidationErrorReport;
import com.qiuyj.commons.validate.ValidationResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-06-07
 */
public class MapValidator extends AbstractValidator<Map> {

  public MapValidator(List<String> validateKeys) {
    super(init(validateKeys));
  }

  @Override
  protected boolean isInstanceTypeEquals(Map validatedObject) {
    // 这里传入进来的参数一定是Map，所以这里直接返回true即可
    return true;
  }

  @Override
  protected ValidationErrorReport doGetValidationErrorReport() {
    MapValidationRule rule = MapValidationRule.class.cast(getValidationRule());
    return rule.getLocalValidationErrorReport();
  }

  @Override
  protected ValidationResult doCreateValidationResult(ValidationErrorReport errorReport) {
    MapValidationErrorReport report = MapValidationErrorReport.class.cast(errorReport);
    return super.doCreateValidationResult(report);
  }

  @Override
  public Class<? extends Map> getValidatedClass() {
    return Map.class;
  }

  private static MapValidationRule init(List<String> validateKeys) {
    if (Objects.isNull(validateKeys) || validateKeys.isEmpty()) {
      throw new IllegalArgumentException("Must specify the field to verify");
    }
    else {
      return new MapValidationRule(validateKeys);
    }
  }
}
