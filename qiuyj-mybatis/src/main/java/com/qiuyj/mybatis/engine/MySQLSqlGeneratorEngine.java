package com.qiuyj.mybatis.engine;

import com.qiuyj.mybatis.MapperMethodResolver;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.SqlProvider;
import com.qiuyj.mybatis.checker.CheckerChain;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public class MySQLSqlGeneratorEngine extends AbstractSqlGeneratorEngine {

  public MySQLSqlGeneratorEngine(CheckerChain chain, SqlProvider sqlProvider, MapperMethodResolver resolver) {
    super(chain, sqlProvider, resolver);
  }

  @Override
  protected void generateSequenceKey(MappedStatement ms, MetaObject msMetaObject, SqlInfo sqlInfo) {
    // do nothing
  }
}
