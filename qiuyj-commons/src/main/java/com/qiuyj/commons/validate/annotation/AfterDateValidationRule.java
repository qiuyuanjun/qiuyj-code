package com.qiuyj.commons.validate.annotation;

import com.qiuyj.commons.StringUtils;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiuyj
 * @since 2018-06-05
 */
public class AfterDateValidationRule extends AnnotationBasedValidationRule<AfterDate> {

  private static final Map<String, DateTimeFormatter> PATTERN_MAP = new ConcurrentHashMap<>();

  static {
    PATTERN_MAP.put("yyyy-MM-dd", DateTimeFormatter.ISO_LOCAL_DATE);
    PATTERN_MAP.put("yyyyMMdd", DateTimeFormatter.BASIC_ISO_DATE);
  }

  private static final List<Class<?>> SUPPORT_VALUE_TYPE =
      Arrays.asList(LocalDate.class, Date.class, long.class);

  public AfterDateValidationRule(AfterDate annotation) {
    super(annotation, !annotation.nullable());
  }

  @Override
  protected boolean doMatchAccordingtoAnnotation(Object value, AfterDate annotationContext) {
    // 如果允许为null，并且值确实为null，那么直接验证通过
    if (annotationContext.nullable() && Objects.isNull(value)) {
      return true;
    }
    else {
      return validateDate(true, value, annotationContext);
    }
  }

  static boolean validateDate(boolean isAfter, Object value, Annotation annotation) {
    LocalDate actualDate = checkAndConvertToLocalDateType(value);
    LocalDate expectDate;
    if (isAfter) {
      AfterDate afterDate = (AfterDate) annotation;
      expectDate = createExpectDateFromAnnotation(afterDate.value(), afterDate.pattern());
      return actualDate.isAfter(expectDate);
    }
    else {
      BeforeDate beforeDate = (BeforeDate) annotation;
      expectDate = createExpectDateFromAnnotation(beforeDate.value(), beforeDate.pattern());
      return actualDate.isBefore(expectDate);
    }
  }

  private static LocalDate createExpectDateFromAnnotation(String dateValue, String pattern) {
    if (StringUtils.isBlank(dateValue)) {
      return LocalDate.now();
    }
    DateTimeFormatter formatter;
    if (PATTERN_MAP.containsKey(pattern)) {
      formatter = PATTERN_MAP.get(pattern);
    }
    else {
      PATTERN_MAP.putIfAbsent(pattern, DateTimeFormatter.ofPattern(pattern));
      formatter = PATTERN_MAP.get(pattern);
    }
    return formatter.parse(dateValue, LocalDate::from);
  }

  private static LocalDate checkAndConvertToLocalDateType(Object value) {
    Class<?> valueType = value.getClass();
    if (SUPPORT_VALUE_TYPE.contains(valueType)) {
      LocalDate convertedValue;
      if (valueType == int.class || valueType == long.class) {
        convertedValue = LocalDate.ofInstant(Instant.ofEpochMilli((long) value), ZoneId.systemDefault());
      }
      else if (valueType == LocalDate.class) {
        convertedValue = (LocalDate) value;
      }
      else {
        convertedValue = LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()).toLocalDate();
      }
      return convertedValue;
    }
    else {
      throw new IllegalArgumentException("Unsupport date type: " + valueType);
    }
  }
}
