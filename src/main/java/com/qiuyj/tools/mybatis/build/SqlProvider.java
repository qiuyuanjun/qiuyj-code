package com.qiuyj.tools.mybatis.build;

import com.qiuyj.tools.mybatis.BeanExampleResolver;
import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiuyj
 * @since 2017/11/21
 */
public class SqlProvider {

  public SqlNode insert(MappedStatement ms, SqlInfo sqlInfo) {
    SQL sql = new SQL() {
      {
        INSERT_INTO(sqlInfo.getTableName());
        INTO_COLUMNS(sqlInfo.getAllColumnsWithoutAlias());
        INTO_VALUES(sqlInfo.getAllColumnValues());
      }
    };
    return new TextSqlNode(sql.toString());
  }

  public SqlNode selectOne(MappedStatement ms, SqlInfo sqlInfo) {
    // 首先判断是否有主键，如果没有主键，那么抛出异常
    checkPrimaryKey(sqlInfo);
    SQL sql = new SQL() {
      {
        SELECT(sqlInfo.getAllColumnsWithAlias());
        FROM(sqlInfo.getTableName());
        WHERE(sqlInfo.getPrimaryKeyCondition());
      }
    };
    return new TextSqlNode(sql.toString());
  }

  public SqlNode delete(MappedStatement ms, SqlInfo sqlInfo) {
    checkPrimaryKey(sqlInfo);
    SQL sql = new SQL() {
      {
        DELETE_FROM(sqlInfo.getTableName());
        WHERE(sqlInfo.getPrimaryKeyCondition());
      }
    };
    return new TextSqlNode(sql.toString());
  }

  public SqlNode batchDelete(MappedStatement ms, SqlInfo sqlInfo) {
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
    return new MixedSqlNode(contents);
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
    contents.add(new StaticTextSqlNode(" WHERE "));
    contents.add(new StaticTextSqlNode(sqlInfo.getPrimaryKey().getDatabaseColumnName()));
    contents.add(new StaticTextSqlNode(" = "));
    contents.add(new StaticTextSqlNode(" #{"));
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