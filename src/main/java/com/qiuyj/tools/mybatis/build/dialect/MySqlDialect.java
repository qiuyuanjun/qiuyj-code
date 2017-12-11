package com.qiuyj.tools.mybatis.build.dialect;

import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ReturnValueWrapper;
import com.qiuyj.tools.mybatis.build.customer.MysqlBatchInsertParameterObjectResolver;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import java.util.StringJoiner;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public class MySqlDialect implements SqlDialect {

  @Override
  public ReturnValueWrapper batchInsert(Configuration configuration, SqlInfo sqlInfo) {
    // 由于批量增加的sql可能会很长，所以这里给一个较大的初始化值
    StringBuilder sqlBuilder = new StringBuilder(256);
    sqlBuilder.append("INSERT INTO ")
              .append(sqlInfo.getTableName());
    StringJoiner joiner = new StringJoiner(",", "(", ")");
    for (String column : sqlInfo.getAllColumnsWithoutAlias()) {
      joiner.add(column);
    }
    sqlBuilder.append(joiner.toString());
    sqlBuilder.append(" VALUES ");
    return new ReturnValueWrapper(new StaticTextSqlNode(sqlBuilder.toString()), MysqlBatchInsertParameterObjectResolver.getInstance());
  }
}
