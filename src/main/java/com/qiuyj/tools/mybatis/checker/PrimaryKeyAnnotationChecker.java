package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.StringUtils;
import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.annotation.Column;
import com.qiuyj.tools.mybatis.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * PrimaryKey注解检查器，如果当前的属性或getter方法有@PrimaryKey，那么跳过下一个检查器检查，否则剩下的所有的检查器都要执行
 * @author qiuyj
 * @since 2017/11/20
 */
public class PrimaryKeyAnnotationChecker implements ConditionChecker {

  @Override
  public ReturnValue doCheck(Field field, SqlInfo sqlInfo, ReturnValue preRv) {
    preRv.intValue = ConditionChecker.CONTINUE_EXECUTION;
    if (!sqlInfo.hasPrimaryKey()) {
      boolean hasPrimaryKey = AnnotationUtils.hasAnnotation(field, PrimaryKey.class);
      if (!hasPrimaryKey) {
        try {
          if (Objects.isNull(preRv.fieldMethod))
            preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field));
          hasPrimaryKey = AnnotationUtils.hasAnnotation(preRv.fieldMethod, PrimaryKey.class);
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
          if (Objects.isNull(preRv.fieldMethod)) {
            try {
              preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field));
            } catch (IllegalStateException e) {
              // ignore
            }
            if (Objects.nonNull(preRv.fieldMethod)) {
              column = AnnotationUtils.findAnnotation(preRv.fieldMethod, Column.class);
              if (Objects.nonNull(column))
                columnName = column.value();
            }
          }
        }
        if (StringUtils.isBlank(columnName))
          columnName = StringUtils.camelCaseToUnderscore(field.getName());
        sqlInfo.setPrimaryKey(
            new PropertyColumnMapping(
                field.getName(),
                columnName,
                sqlInfo.getConfiguration().getTypeHandlerRegistry().getTypeHandler(getFieldJavaType(field))
            )
        );
        preRv.intValue = ConditionChecker.SKIP_ONE;
      }
    }
    return preRv;
  }
}