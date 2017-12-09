package com.qiuyj.tools.mybatis.build;

import com.qiuyj.tools.mybatis.BeanExampleResolver;
import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiuyj
 * @since 2017/11/21
 */
public class SqlProvider {
  private static final String PREPARE_FLAG = "?";

  public ReturnValueWrapper insert(MappedStatement ms, SqlInfo sqlInfo) {
    SQL sql = new SQL() {
      {
        INSERT_INTO(sqlInfo.getTableName());
        INTO_COLUMNS(sqlInfo.getAllColumnsWithoutAlias());
        String[] prepareColumnValues = new String[sqlInfo.getFiledCount()];
        Arrays.fill(prepareColumnValues, PREPARE_FLAG);
        INTO_VALUES(prepareColumnValues);
      }
    };
    List<ParameterMapping> parameterMappings = new ArrayList<>(sqlInfo.getFiledCount());
    TypeHandlerRegistry reg = ms.getConfiguration().getTypeHandlerRegistry();
    ParameterMapping.Builder parameterBuilder;
    int idx = 0;
    for (PropertyColumnMapping mapping : sqlInfo.getPropertyColumnMappings()) {
      parameterBuilder =
          new ParameterMapping.Builder(ms.getConfiguration(),
                                       mapping.getJavaClassPropertyName(),
                                       reg.getTypeHandler(mapping.getJavaType()));
      parameterMappings.add(parameterBuilder.build());
    }
    return new ReturnValueWrapper(new StaticTextSqlNode(sql.toString()), parameterMappings);
  }

  public ReturnValueWrapper selectOne(MappedStatement ms, SqlInfo sqlInfo) {
    // 首先判断是否有主键，如果没有主键，那么抛出异常
    checkPrimaryKey(sqlInfo);
    SQL sql = new SQL() {
      {
        SELECT(sqlInfo.getAllColumnsWithAlias());
        FROM(sqlInfo.getTableName());
        WHERE(sqlInfo.getPrimaryKeyCondition());
      }
    };
    return primaryKeyResolver(sqlInfo.getPrimaryKey(), ms.getConfiguration(), sql.toString());
  }

  public ReturnValueWrapper delete(MappedStatement ms, SqlInfo sqlInfo) {
    checkPrimaryKey(sqlInfo);
    SQL sql = new SQL() {
      {
        DELETE_FROM(sqlInfo.getTableName());
        WHERE(sqlInfo.getPrimaryKeyCondition());
      }
    };
    return primaryKeyResolver(sqlInfo.getPrimaryKey(), ms.getConfiguration(), sql.toString());
  }

  /**
   * 生成以主键作为条件的sql的返回值
   */
  private ReturnValueWrapper primaryKeyResolver(PropertyColumnMapping primaryKey, Configuration config, String sql) {
    List<ParameterMapping> parameterMappings = new ArrayList<>(1);
    parameterMappings.add(new ParameterMapping.Builder(
            config,
            primaryKey.getJavaClassPropertyName(),
            primaryKey.getJavaType()
        ).build()
    );
    return new ReturnValueWrapper(new StaticTextSqlNode(sql), parameterMappings);
  }

  public ReturnValueWrapper batchDelete(MappedStatement ms, SqlInfo sqlInfo) {
    checkPrimaryKey(sqlInfo);
    List<SqlNode> contents = new ArrayList<>();
    contents.add(new StaticTextSqlNode("DELETE FROM"));
    contents.add(new StaticTextSqlNode(sqlInfo.getTableName()));
    contents.add(new StaticTextSqlNode("WHERE"));
    contents.add(new StaticTextSqlNode(sqlInfo.getPrimaryKey().getDatabaseColumnName()));
    contents.add(new StaticTextSqlNode("IN"));
    ForEachSqlNode forEach = new ForEachSqlNode(ms.getConfiguration(),
                                                new StaticTextSqlNode("#{item}"),
                                                "array",
                                                null,
                                                "item",
                                                "(",
                                                ")",
                                                ",");
    contents.add(forEach);
    return new ReturnValueWrapper(new MixedSqlNode(contents));
  }

  public SqlNode update(MappedStatement ms, SqlInfo sqlInfo, Object args) {
    checkPrimaryKey(sqlInfo);
    checkBeanType(sqlInfo.getBeanType(), args);
    BeanExampleResolver exampleResolver = new BeanExampleResolver(args, sqlInfo.getJavaProperties(), sqlInfo.getDatabaseColumns());
    if (!exampleResolver.hasPrimaryKeyAndNotDefault())
      throw new NoPrimaryKeyException("primary key is default value");
    List<PropertyColumnMapping> nonNullColumns = exampleResolver.getWithoutPrimaryKey();
    if (nonNullColumns.isEmpty())
      throw new IllegalStateException("Please update at least one column");
    List<SqlNode> updateSets = new ArrayList<>();
    for (PropertyColumnMapping pcm : nonNullColumns) {
      updateSets.add(new StaticTextSqlNode(buildUpdateSet(pcm.getJavaClassPropertyName(), pcm.getDatabaseColumnName())));
    }
    List<SqlNode> contents = new ArrayList<>();
    contents.add(new StaticTextSqlNode("UPDATE"));
    contents.add(new StaticTextSqlNode(sqlInfo.getTableName()));
    contents.add(new SetSqlNode(ms.getConfiguration(), new MixedSqlNode(updateSets)));
    contents.add(new StaticTextSqlNode("WHERE"));
    contents.add(new StaticTextSqlNode(sqlInfo.getPrimaryKey().getDatabaseColumnName()));
    contents.add(new StaticTextSqlNode("="));
    contents.add(new StaticTextSqlNode("#{"));
    contents.add(new StaticTextSqlNode(sqlInfo.getPrimaryKey().getJavaClassPropertyName()));
    contents.add(new StaticTextSqlNode("}"));
    return new MixedSqlNode(contents);
  }
  private String buildUpdateSet(String java, String database) {
    return new StringBuilder(database)
        .append(" = ")
        .append("#{")
        .append(java)
        .append("}")
        .append(",")
        .toString();
  }

  public SqlNode selectList(MappedStatement ms, SqlInfo sqlInfo, Object args) {
    checkBeanType(sqlInfo.getBeanType(), args);
    BeanExampleResolver resolver = new BeanExampleResolver(args, sqlInfo.getJavaProperties(), sqlInfo.getDatabaseColumns());
    List<PropertyColumnMapping> exampleSelectList = resolver.selectExample();
    if (exampleSelectList.isEmpty())
      throw new IllegalStateException("Please specify at least one condition");
    List<SqlNode> contents = new ArrayList<>();
    SQL sql = new SQL() {
      {
        SELECT(sqlInfo.getAllColumnsWithAlias());
        FROM(sqlInfo.getTableName());
      }
    };
    StringBuilder sqlBuilder = new StringBuilder(sql.toString());
    // 拼接sql
    sqlBuilder.append(" WHERE ");
    int idx = 0;
    PropertyColumnMapping pcm = exampleSelectList.get(idx++);
    sqlBuilder.append(pcm.getDatabaseColumnName())
              .append(" = ? ");
    for (; idx < exampleSelectList.size(); idx++) {
      pcm = exampleSelectList.get(idx);
      sqlBuilder.append("AND ")
                .append(pcm.getDatabaseColumnName())
                .append(" = ? ");
    }
    return new StaticTextSqlNode(sqlBuilder.toString());
  }

  /**
   * 检查如果没有主键，那么就抛出异常
   */
  private void checkPrimaryKey(SqlInfo sqlInfo) {
    if (!sqlInfo.hasPrimaryKey())
      throw new NoPrimaryKeyException();
  }

  /**
   * 检查参数的类型是否和当前Mapper的实体类一致，如果不一致，那么抛出异常
   */
  private void checkBeanType(Class<?> beanType, Object args) {
    if (beanType != args.getClass())
      throw new IllegalArgumentException("Parameter must be " + beanType + " only");
  }
}