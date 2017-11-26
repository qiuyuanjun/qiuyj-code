package com.qiuyj.tools.mybatis.build;

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
    SQL sql = new SQL() {
      {
        DELETE_FROM(sqlInfo.getTableName());
        WHERE(sqlInfo.getPrimaryKeyCondition());
      }
    };
    return new TextSqlNode(sql.toString());
  }

  public SqlNode batchDelete(MappedStatement ms, SqlInfo sqlInfo) {
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
}