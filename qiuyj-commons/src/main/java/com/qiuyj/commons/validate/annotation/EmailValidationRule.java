package com.qiuyj.commons.validate.annotation;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author qiuyj
 * @since 2018-06-05
 */
public class EmailValidationRule extends AnnotationBasedValidationRule<Email> {

  /**
   * 相对完善的邮箱正则表达式
   */
  private static final Pattern EMAIL =
      Pattern.compile("^((\\w+)|(\\w+[!#$%&'*+\\-,./=?^_`{|}~\\w]*[!#$%&'*+\\-,/=?^_`{|}~\\w]))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,10}|[0-9]{1,3})(\\]?)$");

  public EmailValidationRule(Email annotation) {
    super(annotation, false);
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, Email annotationContext) {
    return Objects.isNull(value) || EMAIL.matcher(value.toString()).matches();
  }
}
