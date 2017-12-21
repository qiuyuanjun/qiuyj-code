package com.qiuyj.mybatis.engine;

import com.qiuyj.mybatis.MapperMethodResolver;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.SqlProvider;
import com.qiuyj.mybatis.checker.CheckerChain;
import com.qiuyj.mybatis.key.OracleSequenceDialect;
import com.qiuyj.mybatis.key.SequenceDialect;
import com.qiuyj.mybatis.key.SequenceKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public class OracleSqlGeneratorEngine extends AbstractSqlGeneratorEngine {
  private final SequenceDialect oracleSequenceDialect;

  protected OracleSqlGeneratorEngine(CheckerChain chain, SqlProvider sqlProvider, MapperMethodResolver resolver) {
    super(chain, sqlProvider, resolver);
    oracleSequenceDialect = new OracleSequenceDialect();
  }

  @Override
  protected void generateSequenceKey(MappedStatement ms, MetaObject msMetaObject, SqlInfo sqlInfo) {
    // 如果原来的KeyGenerator不是SequenceKeyGenerator
    // 那么需要设置
    // 否则，无需重复设置
    if (!SequenceKeyGenerator.class.isInstance(ms.getKeyGenerator())) {
      String keyGenStr = oracleSequenceDialect.generateSequenceQueryString(sqlInfo.getSequenceName());
      msMetaObject.setValue("keyGenerator", new SequenceKeyGenerator(sqlInfo, keyGenStr));
    }
  }

}