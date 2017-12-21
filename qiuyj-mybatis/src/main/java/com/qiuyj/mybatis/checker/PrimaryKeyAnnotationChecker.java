package com.qiuyj.mybatis.checker;

import com.qiuyj.commons.AnnotationUtils;
import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.StringUtils;
import com.qiuyj.mybatis.PropertyColumnMapping;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.annotation.Column;
import com.qiuyj.mybatis.annotation.PrimaryKey;
import com.qiuyj.mybatis.key.Sequence;

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
          if (Objects.isNull(preRv.fieldMethod)) {
            preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field));
          }
          hasPrimaryKey = AnnotationUtils.hasAnnotation(preRv.fieldMethod, PrimaryKey.class);
        }
        catch (IllegalStateException e) {
          // ignore
        }
      }
      if (hasPrimaryKey) {
        String columnName = null;
        Column column = AnnotationUtils.findAnnotation(field, Column.class);
        if (Objects.nonNull(column)) {
          columnName = column.value();
        }
        else {
          if (Objects.isNull(preRv.fieldMethod)) {
            try {
              preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field));
            }
            catch (IllegalStateException e) {
              // ignore
            }
            if (Objects.nonNull(preRv.fieldMethod)) {
              column = AnnotationUtils.findAnnotation(preRv.fieldMethod, Column.class);
              if (Objects.nonNull(column)) {
                columnName = column.value();
              }
            }
          }
        }
        if (StringUtils.isBlank(columnName)) {
          columnName = StringUtils.camelCaseToUnderscore(field.getName());
        }
        sqlInfo.setPrimaryKey(
            new PropertyColumnMapping(
                field.getName(),
                columnName,
                sqlInfo.getConfiguration().getTypeHandlerRegistry().getTypeHandler(getFieldJavaType(field))
            )
        );
        // 解析@Sequence注解
        Sequence sequence = AnnotationUtils.findAnnotation(field, Sequence.class);
        if (Objects.isNull(sequence)) {
          if (Objects.isNull(preRv.fieldMethod)) {
            try {
              preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field));
            }
            catch (IllegalStateException e) {
              // ignore
            }
            if (Objects.nonNull(preRv.fieldMethod)) {
              sequence = AnnotationUtils.findAnnotation(preRv.fieldMethod, Sequence.class);
            }
          }
        }
        if (Objects.nonNull(sequence)) {
          String sequenceName = sequence.name();
          if (StringUtils.isBlank(sequenceName)) {
            throw new IllegalStateException("Sequence name can not be empty");
          }
          else {
            sqlInfo.setSequenceName(sequenceName);
          }
        }
        preRv.intValue = ConditionChecker.SKIP_ONE;
      }
    }
    return preRv;
  }
}