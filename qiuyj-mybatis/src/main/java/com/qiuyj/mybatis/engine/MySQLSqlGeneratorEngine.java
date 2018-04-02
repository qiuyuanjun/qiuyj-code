package com.qiuyj.mybatis.engine;

import com.qiuyj.mybatis.SqlInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public class MySQLSqlGeneratorEngine extends AbstractSqlGeneratorEngine {

  @Override
  protected void generateSequenceKey(MappedStatement ms, MetaObject msMetaObject, SqlInfo sqlInfo) {
    // do nothing
  }
}
