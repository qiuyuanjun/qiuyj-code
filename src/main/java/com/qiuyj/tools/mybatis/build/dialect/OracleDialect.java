package com.qiuyj.tools.mybatis.build.dialect;

import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ReturnValueWrapper;
import com.qiuyj.tools.mybatis.build.customer.OracleBatchInsertParameterObjectResolver;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public class OracleDialect implements SqlDialect {

  @Override
  public ReturnValueWrapper batchInsert(Configuration configuration, SqlInfo sqlInfo) {
    return new ReturnValueWrapper(new StaticTextSqlNode("INSERT ALL "), new OracleBatchInsertParameterObjectResolver());
  }
}
