package com.qiuyj.tools.mybatis.build;

import com.qiuyj.tools.mybatis.SqlInfo;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;

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
}