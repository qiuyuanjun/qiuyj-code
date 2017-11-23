package com.qiuyj.tools.mybatis.engine;

import com.qiuyj.tools.mybatis.build.SqlProvider;
import com.qiuyj.tools.mybatis.checker.CheckerChain;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public class MySQLSqlGeneratorEngine extends AbstractSqlGeneratorEngine {

  protected MySQLSqlGeneratorEngine(CheckerChain chain, SqlProvider sqlProvider) {
    super(chain, sqlProvider);
  }
}
