package com.qiuyj.mybatis.sqlbuild;

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
public class OracleSqlBuilder extends AbstractCommonSqlBuilder {

  @Override
  public Object batchInsert(SqlInfo sqlInfo, Object args) {
    List list = (List) ParameterResolver.resolveParameter(args).getParameterValues()[0];
    if (list.isEmpty()) {
      throw new IllegalArgumentException("Method batchInsert()'s parameter can not be an empty list");
    }
    else {
      StringBuilder sql = new StringBuilder(256);
      sql.append("INSERT ALL");
      String join = " INTO " + sqlInfo.getTableName();
      StringBuilder eachBuilder = new StringBuilder();
      StringJoiner joiner = new StringJoiner(",", "(", ")");
      List<ParameterMapping> instanceParameterMappings = new ArrayList<>(sqlInfo.getFieldCount());
      BatchInsertTypeHandler typeHandler = new BatchInsertTypeHandler(sqlInfo);
      ParameterMapping canonic = new ParameterMapping.Builder(
          sqlInfo.getConfiguration(),
          "list",
          typeHandler
      ).build();
      for (String column : sqlInfo.getAllColumnsWithoutAlias()) {
        joiner.add(column);
        instanceParameterMappings.add(canonic);
      }
      eachBuilder.append(joiner.toString());
      eachBuilder.append(" VALUES ");
      joiner = new StringJoiner(",", "(", ")");
      List<ParameterMapping> parameterMappings = new ArrayList<>(sqlInfo.getFieldCount() * list.size());
      for (int idx = 0; idx < sqlInfo.getFieldCount(); idx++) {
        joiner.add(AbstractCommonSqlBuilder.PREPARE_FLAG);
      }
      eachBuilder.append(joiner.toString());
      joiner = new StringJoiner(join);
      for (int idx = 0; idx < list.size(); idx++) {
        joiner.add(eachBuilder.toString());
        parameterMappings.addAll(instanceParameterMappings);
      }
      sql.append(join)
         .append(joiner)
         .append(" SELECT 1 FROM DUAL");
      return new StaticSqlSource(sqlInfo.getConfiguration(), sql.toString(), parameterMappings);
    }
  }
}