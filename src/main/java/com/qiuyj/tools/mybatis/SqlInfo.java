package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.StringUtils;
import com.qiuyj.tools.mybatis.annotation.Column;
import com.qiuyj.tools.mybatis.annotation.PrimaryKey;
import com.qiuyj.tools.mybatis.annotation.Table;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public final class SqlInfo {
  private String tableName;
  private final List<PropertyColumnMapping> withoutPrimaryKey = new ArrayList<>();
  private PropertyColumnMapping primaryKey;

  public SqlInfo(Class<? extends Mapper> mapperClass) {
    // 得到泛型，这里Mapper会有两个泛型，第一个表示主键，第二个才是正真运行时候的实体类
    // public interface Mapper<ID, T> {}
    Class<?> beanType = ReflectionUtils.getParameterizedTypesAsClass(mapperClass)[1];
    // 首先得到表名
    Table table = AnnotationUtils.findAnnotation(beanType, Table.class);
    if (Objects.nonNull(table))
      tableName = table.value();
    if (Objects.isNull(tableName))
      tableName = StringUtils.camelCaseToUnderscore(beanType.getSimpleName());
    // 接着得到主键名和其他列名
    Field[] allDeclaredFields = ClassUtils.getAllDeclaredFields(beanType);
    for (Field field : allDeclaredFields) {
      boolean primaryKeyFlag = false;
      if (Objects.isNull(primaryKey)) {
        if (AnnotationUtils.hasAnnotation(field, PrimaryKey.class)) {
          primaryKey = new PropertyColumnMapping();
          primaryKeyFlag = true;
        } else {
          try {
            // 得到对应的get方法
            Method getter = ReflectionUtils.getDeclaredMethod(beanType, fieldToGetterName(field), field.getType());
            // 判断在getter方法上是否有PrimaryKey注解
            if (AnnotationUtils.hasAnnotation(getter, PrimaryKey.class)) {
              primaryKey = new PropertyColumnMapping();
              primaryKeyFlag = true;
            }
          } catch (Throwable throwable) {
            // ignore
          }
        }
      }
      // 接下来处理Column
      Column currCol = AnnotationUtils.findAnnotation(field, Column.class);
      String columnName = null;
      if (Objects.nonNull(currCol))
        columnName = currCol.value();
      if (Objects.isNull(columnName)) {
        // 此时去看对应的getter方法上面是否有Column注解
        try {
          Method getter = ReflectionUtils.getDeclaredMethod(beanType, fieldToGetterName(field), field.getType());
          Column methodCol = AnnotationUtils.findAnnotation(getter, Column.class);
          if (Objects.nonNull(methodCol))
            columnName = methodCol.value();
        } catch (IllegalStateException e) {
          // ignore
        }
      }
      if (Objects.isNull(columnName))
        columnName = StringUtils.camelCaseToUnderscore(field.getName());
      if (primaryKeyFlag) {
        // 主键
        primaryKey.setDatabaseColumnName(columnName);
        primaryKey.setJavaClassPropertyName(field.getName());
      } else {
        PropertyColumnMapping pcm = new PropertyColumnMapping(field.getName(), columnName);
        withoutPrimaryKey.add(pcm);
      }
    }
  }

  /**
   * 将一个属性按照javabean规范转换成对应的getter方法名
   */
  private String fieldToGetterName(Field field) {
    char[] chs = field.getName().toCharArray();
    chs[0] = Character.toUpperCase(chs[0]);
    StringBuilder sb;
    if (field.getType() == Boolean.TYPE || field.getType() == Boolean.class)
      sb = new StringBuilder("is");
    else
      sb = new StringBuilder("get");
    sb.append(chs);
    return sb.toString();
  }

  /**
   * 判断当前的表是否有主键
   */
  public boolean hasPrimaryKey() {
    return Objects.nonNull(primaryKey);
  }

}