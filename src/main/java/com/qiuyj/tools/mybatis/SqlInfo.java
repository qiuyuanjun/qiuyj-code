package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.annotation.Column;
import com.qiuyj.tools.mybatis.annotation.PrimaryKey;
import com.qiuyj.tools.mybatis.annotation.Table;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.beans.Introspector;
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
    // 得到泛型
    Class<?> beanType = ReflectionUtils.getParameterizedTypesAsClass(mapperClass)[0];
    // 首先得到表名
    Table table = AnnotationUtils.findAnnotation(beanType, Table.class);
    if (Objects.nonNull(table))
      tableName = table.value();
    if (Objects.isNull(tableName))
      tableName = toUnderscoreName(beanType.getSimpleName());
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
            Method getter = ReflectionUtils.getDeclaredMethod(beanType, fieldToGetterName(field.getName()), field.getType());
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
          Method getter = ReflectionUtils.getDeclaredMethod(beanType, fieldToGetterName(field.getName()), field.getType());
          Column methodCol = AnnotationUtils.findAnnotation(getter, Column.class);
          if (Objects.nonNull(methodCol))
            columnName = methodCol.value();
        } catch (IllegalStateException e) {
          // ignore
        }
      }
      if (Objects.isNull(columnName))
        columnName = toUnderscoreName(field.getName());
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

  private String fieldToGetterName(String fieldName) {
    char[] chs = fieldName.toCharArray();
    chs[0] = Character.toUpperCase(chs[0]);
    StringBuilder sb = new StringBuilder("get");
    sb.append(chs);
    return sb.toString();
  }

  private String toUnderscoreName(String originName) {
    int len = originName.length();
    StringBuilder sb = new StringBuilder();
    if (len > 1) {
      char[] chs = originName.toCharArray();
      int i = 0;
      char c = chs[i];
      if (Character.isUpperCase(c) && Character.isUpperCase(chs[i + 1])) {
        i = 1;
        do {
          if (++i >= len)
            break;
          c = chs[i];
        } while (Character.isUpperCase(c));
        if (i == len) {
          sb.append(chs);
          return sb.toString();
        } else {
          sb.append(chs, 0, --i);
          sb.append("_");
          chs[i] = Character.toLowerCase(chs[i]);
        }
      } else {
        sb.append(Character.toLowerCase(c));
        i++;
      }
      for (; i < len; i++) {
        c = chs[i];
        if (Character.isUpperCase(c) && Character.isLowerCase(chs[i - 1])) {
          sb.append("_");
          sb.append(Character.toLowerCase(c));
        } else
          sb.append(c);
      }
    } else
      sb.append(Introspector.decapitalize(originName));
    return sb.toString();
  }

  /**
   * 判断当前的表是否有主键
   */
  public boolean hasPrimaryKey() {
    return Objects.nonNull(primaryKey);
  }

}