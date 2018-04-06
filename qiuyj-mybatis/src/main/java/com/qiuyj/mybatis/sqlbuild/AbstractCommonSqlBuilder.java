package com.qiuyj.mybatis.sqlbuild;

import com.qiuyj.mybatis.BeanExampleResolver;
import com.qiuyj.mybatis.PropertyColumnMapping;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.typehandler.BatchDeleteTypeHandler;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiuyj
 * @since 2018/4/1
 */
public abstract class AbstractCommonSqlBuilder implements SqlBuilder {

  public static final String PREPARE_FLAG = "?";

  @Override
  public Object insert(SqlInfo sqlInfo, Object args) {
    String[] prepareColumnValues = new String[sqlInfo.getFieldCount()];
    Arrays.fill(prepareColumnValues, PREPARE_FLAG);
    String sql =  new SQL().INSERT_INTO(sqlInfo.getTableName())
                           .INTO_COLUMNS(sqlInfo.getAllColumnsWithoutAlias())
                           .INTO_VALUES(prepareColumnValues)
                           .toString();
    List<ParameterMapping> parameterMappings = new ArrayList<>(sqlInfo.getFieldCount());
    TypeHandlerRegistry reg = sqlInfo.getConfiguration().getTypeHandlerRegistry();
    ParameterMapping.Builder parameterBuilder;
    for (PropertyColumnMapping mapping : sqlInfo.getPropertyColumnMappings()) {
      parameterBuilder =
          new ParameterMapping.Builder(sqlInfo.getConfiguration(),
              mapping.getJavaClassPropertyName(),
              mapping.getTypeHandler());
      parameterMappings.add(parameterBuilder.build());
    }
    return new StaticSqlSource(sqlInfo.getConfiguration(), sql, parameterMappings);
  }

  @Override
  public Object selectOne(SqlInfo sqlInfo, Object args) {
    // 首先判断是否有主键，如果没有主键，那么抛出异常
    checkPrimaryKey(sqlInfo);
    return new SQL().SELECT(sqlInfo.getAllColumnsWithAlias())
                    .FROM(sqlInfo.getTableName())
                    .WHERE(sqlInfo.getPrimaryKeyCondition())
                    .toString();
  }

  @Override
  public Object selectList(SqlInfo sqlInfo, Object args) {
    checkBeanType(sqlInfo.getBeanType(), args);
    BeanExampleResolver resolver = new BeanExampleResolver(args, sqlInfo.getJavaProperties(), sqlInfo.getDatabaseColumns());
    List<PropertyColumnMapping> exampleSelectList = resolver.selectExample();
    if (exampleSelectList.isEmpty()) {
      throw new IllegalStateException("Please specify at least one condition.");
    }
    else {
      SQL sql = new SQL().SELECT(sqlInfo.getAllColumnsWithAlias())
                         .FROM(sqlInfo.getTableName());
      List<ParameterMapping> parameterMappings = new ArrayList<>(exampleSelectList.size());
      for (PropertyColumnMapping pcm : exampleSelectList) {
        parameterMappings.add(new ParameterMapping.Builder(
            sqlInfo.getConfiguration(),
            pcm.getJavaClassPropertyName(),
            pcm.getValue().getClass()
        ).build());
        sql.WHERE(pcm.getDatabaseColumnName() + " = ?");
      }
      return new StaticSqlSource(sqlInfo.getConfiguration(), sql.toString(), parameterMappings);
    }
  }

  @Override
  public Object update(SqlInfo sqlInfo, Object args) {
    checkPrimaryKey(sqlInfo);
    checkBeanType(sqlInfo.getBeanType(), args);
    BeanExampleResolver exampleResolver = new BeanExampleResolver(args, sqlInfo.getJavaProperties(), sqlInfo.getDatabaseColumns());
    if (!exampleResolver.hasPrimaryKeyAndNotDefault()) {
      throw new NoPrimaryKeyException("Primary key is default value.");
    }
    else {
      List<PropertyColumnMapping> nonNullColumns = exampleResolver.getWithoutPrimaryKey();
      if (nonNullColumns.isEmpty()) {
        throw new IllegalStateException("Please update at least one column");
      }
      else {
        List<ParameterMapping> parameterMappings = new ArrayList<>(nonNullColumns.size() + 1);
        SQL sql = new SQL();
        sql.UPDATE(sqlInfo.getTableName());
        for (PropertyColumnMapping pcm : nonNullColumns) {
          sql.SET(pcm.getDatabaseColumnName() + " = ?");
          parameterMappings.add(new ParameterMapping.Builder(
              sqlInfo.getConfiguration(),
              pcm.getJavaClassPropertyName(),
              pcm.getValue().getClass()
          ).build());
        }
        sql.WHERE(sqlInfo.getPrimaryKey().getDatabaseColumnName() + " = ?");
        parameterMappings.add(new ParameterMapping.Builder(
            sqlInfo.getConfiguration(),
            sqlInfo.getPrimaryKey().getJavaClassPropertyName(),
            sqlInfo.getPrimaryKey().getTypeHandler()
        ).build());
        return new StaticSqlSource(sqlInfo.getConfiguration(), sql.toString(), parameterMappings);
      }
    }
  }

  @Override
  public Object delete(SqlInfo sqlInfo, Object args) {
    checkPrimaryKey(sqlInfo);
    return new SQL().DELETE_FROM(sqlInfo.getTableName())
                    .WHERE(sqlInfo.getPrimaryKeyCondition())
                    .toString();
  }

  @Override
  public Object batchDelete(SqlInfo sqlInfo, Object args) {
    checkPrimaryKey(sqlInfo);
    Object paramValue = ParameterResolver.resolveParameter(args).getParameterValues()[0];
    int len = Array.getLength(paramValue);
    if (len == 0) {
      throw new IllegalArgumentException("Method batchDelete() parameter can not be an empty array");
    }
    else {
      StringBuilder sb = new StringBuilder("DELETE FROM ")
          .append(sqlInfo.getTableName())
          .append(" WHERE ")
          .append(sqlInfo.getPrimaryKey().getDatabaseColumnName())
          .append(" IN (");
      List<ParameterMapping> parameterMappings = new ArrayList<>(len);
      ParameterMapping canonic = new ParameterMapping.Builder(
          sqlInfo.getConfiguration(),
          "array",
          BatchDeleteTypeHandler.getInstance()
      ).build();
      for (int i = 0; i < len; i++) {
        parameterMappings.add(canonic);
        sb.append(PREPARE_FLAG);
        sb.append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.append(")");
      return new StaticSqlSource(sqlInfo.getConfiguration(), sb.toString(), parameterMappings);
    }
  }

  /**
   * 检查如果没有主键，那么就抛出异常
   */
  private void checkPrimaryKey(SqlInfo sqlInfo) {
    if (!sqlInfo.hasPrimaryKey()) {
      throw new NoPrimaryKeyException();
    }
  }

  /**
   * 检查参数的类型是否和当前Mapper的实体类一致，如果不一致，那么抛出异常
   */
  private void checkBeanType(Class<?> beanType, Object args) {
    if (beanType != args.getClass()) {
      throw new IllegalArgumentException("Parameter must be " + beanType + " only");
    }
  }
}