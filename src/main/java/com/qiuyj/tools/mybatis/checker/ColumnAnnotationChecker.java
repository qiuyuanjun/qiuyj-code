package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.StringUtils;
import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.annotation.Column;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Column注解检查器，主要是检查当前属性是否有@Column，不管怎么样都会继续执行剩下的检查器
 * @author qiuyj
 * @since 2017/11/21
 */
public class ColumnAnnotationChecker implements ConditionChecker {

  @Override
  public int doCheck(Field field, SqlInfo sqlInfo) {
    String columnName = null;
    Column column = AnnotationUtils.findAnnotation(field, Column.class);
    if (Objects.nonNull(column))
      columnName = column.value();
    else {
      // 查找对应的getter方法
      try {
        Method getter = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field), field.getType());
        column = AnnotationUtils.findAnnotation(getter, Column.class);
        if (Objects.nonNull(column))
          columnName = column.value();
      } catch (IllegalStateException e) {
        // ingore
      }
    }
    if (StringUtils.isBlank(columnName))
      columnName = StringUtils.camelCaseToUnderscore(field.getName());
    sqlInfo.addPropertyColumn(new PropertyColumnMapping(field.getName(), columnName));
    return ConditionChecker.CONTINUE_EXECUTION;
  }
}