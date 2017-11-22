package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.StringUtils;
import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.annotation.Column;
import com.qiuyj.tools.mybatis.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * PrimaryKey注解检查器，如果当前的属性或getter方法有@PrimaryKey，那么跳过下一个检查器检查，否则剩下的所有的检查器都要执行
 * @author qiuyj
 * @since 2017/11/20
 */
public class PrimaryKeyAnnotationChecker implements ConditionChecker {

  @Override
  public int doCheck(Field field, SqlInfo sqlInfo) {
    boolean hasPrimaryKey = AnnotationUtils.hasAnnotation(field, PrimaryKey.class);
    Method getter = null;
    if (!hasPrimaryKey) {
      try {
        getter = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field), field.getType());
        hasPrimaryKey = AnnotationUtils.hasAnnotation(getter, PrimaryKey.class);
      } catch (IllegalStateException e) {
        // ignore
      }
    }
    if (hasPrimaryKey) {
      String columnName = null;
      Column column = AnnotationUtils.findAnnotation(field, Column.class);
      if (Objects.nonNull(column))
        columnName = column.value();
      else {
        if (Objects.isNull(getter)) {
          try {
            getter = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field), field.getType());
          } catch (IllegalStateException e) {
            // ignore
          }
          if (Objects.nonNull(getter)) {
            column = AnnotationUtils.findAnnotation(getter, Column.class);
            if (Objects.nonNull(column))
              columnName = column.value();
          }
        }
      }
      if (StringUtils.isBlank(columnName))
        columnName = StringUtils.camelCaseToUnderscore(field.getName());
      sqlInfo.setPrimaryKey(new PropertyColumnMapping(field.getName(), columnName));
      return ConditionChecker.SKIP_ONE;
    } else
      return ConditionChecker.CONTINUE_EXECUTION;
  }
}