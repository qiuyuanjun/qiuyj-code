package com.qiuyj.mybatis.sqlbuild;

import com.qiuyj.mybatis.PropertyColumnMapping;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.typehandler.BatchInsertTypeHandler;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author qiuyj
 * @since 2018/4/2
 */
public class MysqlSqlBuilder extends AbstractCommonSqlBuilder {

  @Override
  public Object batchInsert(SqlInfo sqlInfo, Object args) {
    List list = (List) ParameterResolver.resolveParameter(args).getParameterValues()[0];
    if (list.isEmpty()) {
      throw new IllegalArgumentException("Method batchInsert parameter can not be an empty list");
    }
    else {
      // 由于批量增加的sql可能会很长，所以这里给一个较大的初始化值
      StringBuilder sqlBuilder = new StringBuilder(256);
      sqlBuilder.append("INSERT INTO ");
      sqlBuilder.append(sqlInfo.getTableName());
      StringJoiner joiner = new StringJoiner(",", "(", ")");
      for (String column : sqlInfo.getAllColumnsWithoutAlias()) {
        joiner.add(column);
      }
      sqlBuilder.append(joiner.toString());
      sqlBuilder.append(" VALUES ");
      StringJoiner grammaJoiner = new StringJoiner(",");
      StringJoiner valueJoiner = new StringJoiner(",", "(", ")");
      List<ParameterMapping> singleInstanceParameterMapping = new ArrayList<>(sqlInfo.getFieldCount());
      BatchInsertTypeHandler typeHandler = new BatchInsertTypeHandler(sqlInfo);
      ParameterMapping canonic = new ParameterMapping.Builder(
          sqlInfo.getConfiguration(),
          "list",
          typeHandler
      ).build();
      for (PropertyColumnMapping propertyColumnMapping : sqlInfo.getPropertyColumnMappings()) {
        valueJoiner.add(AbstractCommonSqlBuilder.PREPARE_FLAG);
        singleInstanceParameterMapping.add(canonic);
      }
      List<ParameterMapping> allParameterMappings = new ArrayList<>(sqlInfo.getFieldCount() * list.size());
      String value = valueJoiner.toString();
      for (int i = 0; i < list.size(); i++) {
        grammaJoiner.add(value);
        allParameterMappings.addAll(singleInstanceParameterMapping);
      }
      sqlBuilder.append(grammaJoiner.toString());
      return new StaticSqlSource(sqlInfo.getConfiguration(), sqlBuilder.toString(), allParameterMappings);
    }
  }
}