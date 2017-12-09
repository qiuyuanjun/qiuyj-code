package com.qiuyj.tools.mybatis;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 自定义SqlSource，只用于用于通用Mapper
 * @author qiuyj
 * @since 2017/12/9
 */
public class MapperSqlSource implements SqlSource {
  private SqlSource delegate;

  public MapperSqlSource(SqlSource delegate) {
    this.delegate = delegate;
  }

  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    return delegate.getBoundSql(parameterObject);
  }
}