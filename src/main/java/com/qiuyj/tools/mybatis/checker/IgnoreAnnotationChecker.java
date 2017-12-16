package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.annotation.Ignore;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Ignore注解检查器，检查当前的属性或对应的getter方法上是否有@Ignore，如果有，那么不应该继续执行剩下的检查器
 * @author qiuyj
 * @since 2017/11/20
 */
public class IgnoreAnnotationChecker implements ConditionChecker {

  @Override
  public ReturnValue doCheck(Field field, SqlInfo sqlInfo, ReturnValue preRv) {
    boolean hasIgnoreAnnotation = AnnotationUtils.hasAnnotation(field, Ignore.class);
    if (!hasIgnoreAnnotation) {
      // 如果当前属性（Field）上没有@Ignore，那么判断对应的getter方法上是否有@Ignore
      try {
        preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field));
      } catch (IllegalStateException e) {
        // ignore
      }
      if (Objects.nonNull(preRv.fieldMethod))
        hasIgnoreAnnotation = AnnotationUtils.hasAnnotation(preRv.fieldMethod, Ignore.class);
    }
    preRv.intValue = hasIgnoreAnnotation ? ConditionChecker.BREAK_CURRENT : ConditionChecker.CONTINUE_EXECUTION;
    return preRv;
  }
}