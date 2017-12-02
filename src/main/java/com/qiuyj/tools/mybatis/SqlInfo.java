package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.checker.CheckerChain;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public final class SqlInfo {
  private String tableName;
  private final List<PropertyColumnMapping> withoutPrimaryKey = new ArrayList<>();
  private PropertyColumnMapping primaryKey;
  private final Class<?> beanType;

  /*
   * 接下来的所有属性均为辅助属性
   */
  private String primaryKeyCondition;
  private String[] allColumnsWithAlias;
  private String[] allColumnsWithoutAlias;
  private String[] allColumnValues;

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
    /*
     * 辅助属性，主键作为唯一一个条件的字符串
     */
    PrimaryKeyCondition();
    AllColumnsWithAlias();
    AllColumnsWithoutAlias();
    AllColumnValues();
  }

  private void AllColumnValues() {
    String[] rs = new String[withoutPrimaryKey.size() + 1];
    rs[0] = buildColumnValues(getPrimaryKey());
    int i = 1;
    for (PropertyColumnMapping pcm : withoutPrimaryKey) {
      rs[i++] = buildColumnValues(pcm);
    }
    allColumnValues = rs;
  }

  private String buildColumnValues(PropertyColumnMapping mapping) {
    return new StringBuilder("#{")
        .append(mapping.getJavaClassPropertyName())
        .append("}")
        .toString();
  }

  private void PrimaryKeyCondition() {
    primaryKeyCondition = primaryKey.getDatabaseColumnName() + " = #{param1}";
  }

  private void AllColumnsWithoutAlias() {
    List<String> list = withoutPrimaryKey.parallelStream()
        .map(PropertyColumnMapping::getDatabaseColumnName)
        .collect(Collectors.toList());
    list.add(0, primaryKey.getDatabaseColumnName());
    allColumnsWithoutAlias = list.toArray(new String[list.size()]);
  }

  private void AllColumnsWithAlias() {
    List<String> list = withoutPrimaryKey.parallelStream()
        .map(PropertyColumnMapping::toString)
        .collect(Collectors.toList());
    list.add(0, primaryKey.toString());
    allColumnsWithAlias = list.toArray(new String[list.size()]);
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

  public void addPropertyColumn(PropertyColumnMapping column) {
    Objects.requireNonNull(column);
    withoutPrimaryKey.add(column);
  }

  public String[] getAllColumnsWithAlias() {
    return allColumnsWithAlias;
  }

  public String[] getAllColumnsWithoutAlias() {
    return allColumnsWithoutAlias;
  }

  public List<PropertyColumnMapping> getWithoutPrimaryKey() {
    return withoutPrimaryKey;
  }

  public String getPrimaryKeyCondition() {
    return primaryKeyCondition;
  }

  public String[] getAllColumnValues() {
    return allColumnValues;
  }

  /**
   * 得到当前所有的java属性名称
   */
  public List<String> getJavaProperties() {
    List<String> rt = withoutPrimaryKey.stream().map(PropertyColumnMapping::getJavaClassPropertyName).collect(Collectors.toList());
    if (hasPrimaryKey())
      rt.add(0, primaryKey.getJavaClassPropertyName());
    else
      rt.add(0, null);
    return rt;
  }

  /**
   * 得到当前所有的数据库名称
   */
  public List<String> getDatabaseColumns() {
    List<String> rt = withoutPrimaryKey.stream().map(PropertyColumnMapping::getDatabaseColumnName).collect(Collectors.toList());
    if (hasPrimaryKey())
      rt.add(0, primaryKey.getDatabaseColumnName());
    else
      rt.add(0, null);
    return rt;
  }
}