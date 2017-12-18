package com.qiuyj.mybatis.checker;

import com.qiuyj.commons.AnnotationUtils;
import com.qiuyj.commons.StringUtils;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.annotation.Table;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Table注解的检查器，得到对应的表名，不管怎么样，都会继续执行剩下的所有检查器
 * @author qiuyj
 * @since 2017/11/20
 */
public class TableAnnotationChecker implements ConditionChecker {

  @Override
  public ReturnValue doCheck(Field field, SqlInfo sqlInfo, ReturnValue rv) {
    /*
     * 这么判断是为了防止在循环所有的Field的时候都要执行下面的代码
     */
    if (StringUtils.isBlank(sqlInfo.getTableName())) {
      Class<?> beanType = sqlInfo.getBeanType();
      Table table = AnnotationUtils.findAnnotation(beanType, Table.class);
      if (Objects.nonNull(table))
        sqlInfo.setTableName(table.value());
      if (StringUtils.isBlank(sqlInfo.getTableName()))
        sqlInfo.setTableName(StringUtils.camelCaseToUnderscore(beanType.getSimpleName()));
    }
    return new ReturnValue(ConditionChecker.CONTINUE_EXECUTION);
  }
}