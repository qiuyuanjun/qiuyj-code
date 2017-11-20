package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.checker.CheckerChain;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.lang.reflect.Field;
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
  private final Class<?> beanType;

  public SqlInfo(Class<? extends Mapper> mapperClass, final CheckerChain chain) {
    // 得到泛型，这里Mapper会有两个泛型，第一个表示主键，第二个才是正真运行时候的实体类
    // public interface Mapper<ID, T> {}
    beanType = ReflectionUtils.getParameterizedTypesAsClass(mapperClass)[1];
    Field[] allDeclaredFields = ClassUtils.getAllDeclaredFields(beanType);
    /*
     * 对每一个field执行检查器链
     */
    for (Field field : allDeclaredFields) {
      chain.checkAll(field, this);
    }
  }

  /**
   * 判断当前的表是否有主键
   */
  public boolean hasPrimaryKey() {
    return Objects.nonNull(primaryKey);
  }

  public Class<?> getBeanType() {
    return beanType;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public PropertyColumnMapping getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(PropertyColumnMapping primaryKey) {
    this.primaryKey = primaryKey;
  }
}