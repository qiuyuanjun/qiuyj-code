package com.qiuyj.commons.validate.annotation;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author qiuyj
 * @since 2018-06-05
 */
public class MobileValidationRule extends AnnotationBasedValidationRule<Mobile> {

  /**
   * 相对完善的手机正则表达式
   */
  private static final Pattern MOBILE = Pattern.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");

  public MobileValidationRule(Mobile annotation) {
    super(annotation, false);
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, Mobile annotationContext) {
    return Objects.isNull(value) || MOBILE.matcher(value.toString()).matches();
  }
}
