package com.qiuyj.mybatis.sqlbuild.dialect;

import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.ReturnValueWrapper;
import com.qiuyj.mybatis.sqlbuild.customer.OracleBatchInsertParameterObjectResolver;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public class OracleDialect implements SqlDialect {

  @Override
  public ReturnValueWrapper batchInsert(Configuration configuration, SqlInfo sqlInfo) {
    return new ReturnValueWrapper(new StaticTextSqlNode("INSERT ALL"), OracleBatchInsertParameterObjectResolver.getInstance());
  }
}
